package no.ngu.dspace;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import no.ngu.dspace.model.*;
import no.ngu.dspace.model.Collection;
import no.ngu.dspace.utils.GsonCreator;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * This client works on the DSPACE v6.x REST-API
 * https://wiki.lyrasis.org/display/DSDOC6x/REST+API
 *
 *
 */
public class DSpaceClient {

    private final String urlPrefix;
    private final String username;
    private final String password;

    private final String ACCEPT_HEADER = "application/json";
    private final String ACTION_TEST_REST = "test";
    private final String ACTION_LOGIN = "login";
    private final String ACTION_LOGOUT = "logout";
    private final String ACTION_STATUS = "status";
    private final String ACTION_COLLECTION = "collections";
    private final String ACTION_COMMUNITIES = "communities";
    private final String ACTION_ITEMS = "items";
    private final String ACTION_FIND_BY_METADATA_FIELD = "items/find-by-metadata-field";
    private String JSESSIONID=null;
    private BasicCookieStore cookieStore;
    private BasicClientCookie cookie;

    // Constructor
    public DSpaceClient(String urlPrefix, String username, String password) {
        this.cookieStore = new BasicCookieStore();
        this.urlPrefix = urlPrefix;
        this.username = username;
        this.password = password;
    }

    /**
     * Automatically map to/from POJOs and JSON
     * @return
     */
    private Gson gson() {
        GsonBuilder b = GsonCreator.builder();
        return b.create();
    }

    /**
     * Helper method generating correct REST endpoint URLs
     */
    private String getUrl(String action) {
        return String.format("%s/%s",this.urlPrefix,action);
    }

    /**
     * Set the mimetype of a given file based on file suffix
     * https://wiki.lyrasis.org/display/DSDOC4x/Metadata+and+Bitstream+Format+Registries#MetadataandBitstreamFormatRegistries-DefaultBitstreamFormatRegistry
     * @param file
     * @return
     */
    private String getMimetype(File file) {
        String filename = file.getName().toLowerCase();
        String result = null;
        if (filename.endsWith(".pdf")) {
            result = String.format("application/%s", "pdf");
        } else if (filename.endsWith(".zip")) {
            result =  String.format("application/%s", "zip");
        } else if (filename.endsWith(".png")) {
            result = String.format("image/%s", "png");
        } else if (filename.endsWith(".jpg")) {
            result = String.format("image/%s", "jpeg");
        } else {
            result = "application/octet-stream";
        }
        return result;
    }

    /**
     * Plain HTTP GET Method
     * @param url
     * @return
     * @throws IOException
     */
    private String httpGET(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("Accept", ACCEPT_HEADER);
        if (JSESSIONID!= null && !JSESSIONID.isEmpty()) {
            conn.addRequestProperty("Cookie",JSESSIONID);
        }

        if (conn.getResponseCode() != 200) {
            throw new IOException(
                    "GET got " + conn.getResponseCode() + " " + conn.getResponseMessage() + " from " + url);
        }
        String s = IOUtils.toString(conn.getInputStream());
        return s;
    }

    /**
     * Plain HTTP PUT Method
     * @param url
     * @param accept_header
     * @param json
     * @return
     * @throws Exception
     */
    private String httpPUT(String url, String accept_header, String json) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPut request = new HttpPut(url);
        if (this.JSESSIONID != null) {
            request.addHeader("Cookie","JSESSIONID="+JSESSIONID);
        } else {
            System.err.println("No JSESSIONID found or JSESSIONID is empty");
        }
        request.addHeader("Accept", ACCEPT_HEADER);
        request.addHeader("Content-type",ACCEPT_HEADER);
        StringEntity stringEntity = new StringEntity(json);
        request.setEntity(stringEntity);

        HttpResponse response = httpclient.execute(request);
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

        if (response.getStatusLine().getStatusCode() != 200) {
            System.err.println("Error code: " + response.getStatusLine().getReasonPhrase());
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        httpclient.close();
        return String.valueOf(result);

    }

    /**
     * HTTP Post data to url
     * @param url
     * @param contentType
     * @param data
     * @return
     * @throws IOException
     */
    private String httpPOST(String url, String contentType, String data) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);

        StringEntity stringEntity = new StringEntity(data,ContentType.APPLICATION_JSON);
        request.setEntity(stringEntity);
        if (this.JSESSIONID != null) {
            request.setHeader("Cookie","JSESSIONID="+JSESSIONID);
        }
        request.setHeader("Accept", ACCEPT_HEADER);
        request.setHeader("Content-type",ACCEPT_HEADER);

        HttpResponse response = httpclient.execute(request);
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        if (response.getStatusLine().getStatusCode() != 200) {
            System.err.println("Error msg: " + response.getStatusLine().getReasonPhrase());
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + ". Reason: " + response.getStatusLine().getReasonPhrase());
        }
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        httpclient.close();
        return String.valueOf(result);
    }

    /**
     * HttpPost a File (Bitstream) to an Item
     * @param url
     * @param file
     * @return
     * @throws Exception
     */
    private String httpPOSTFile(String url, File file) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        FileEntity postData = new FileEntity(file, ContentType.create(getMimetype(file)));
        HttpUriRequest postRequest = RequestBuilder.post(url).setEntity(postData).build();

        if (JSESSIONID != null) {
            postRequest.addHeader("Cookie","JSESSIONID="+this.JSESSIONID);
        }
        postRequest.addHeader("Accept", ACCEPT_HEADER);

        HttpResponse response = httpclient.execute(postRequest);
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

        //Throw runtime exception if status code isn't 200
        if (response.getStatusLine().getStatusCode() != 200) {
            if (response.getStatusLine().getStatusCode() != 500) {
                System.err.println("Internal Server Error while uploading file.Reason: " + response.getStatusLine().getReasonPhrase());
            }
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        httpclient.close();
        return String.valueOf(result);
    }

    /**
     * HTTP Delete - To be used carefully!
     * DSpace suggest using Withdraw instead, but not able to Withdraw as HttpPut in DSpace <= 6.x
     * @param url
     * @return
     * @throws IOException
     */
    private boolean httpDelete(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpDelete request = new HttpDelete(url);
        if (JSESSIONID != null) {
            request.addHeader("Cookie","JSESSIONID="+this.JSESSIONID);
        }
        HttpResponse response = httpclient.execute(request);
        int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && status < 300) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * Login
     * @return
     * @throws Exception
     */
    public boolean login() throws Exception {
        String url = getUrl(ACTION_LOGIN);
        boolean loginOk = false;
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("email",this.username);
        postParameters.put("password",this.password);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("email", this.username));
        params.add(new BasicNameValuePair("password", this.password));

        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);

            httpPost.setEntity(new UrlEncodedFormEntity(params));
            httpPost.addHeader("Content-Type","application/x-www-form-urlencoded");
            CloseableHttpResponse httpResponse = httpclient.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                loginOk = true;
            }
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                if ("Set-Cookie".equalsIgnoreCase(header.getName())) {
                    BasicClientCookie cookie = parseCookieInformation(header.getValue());
                    cookieStore.addCookie(cookie);
                    this.JSESSIONID = cookie.getValue();
                    JSESSIONID = header.getValue().split(";")[0].split("=")[1];
                    System.out.println("Found JSESSIONID: " + JSESSIONID);
                }
            }
            httpclient.close();
        } catch (Exception e) {
            loginOk = false;
            System.err.println("Error occured while trying to login: " + e.getMessage());
        }
        return loginOk;
    }

    /**
     * Logot - deleting cookies as well
     * @throws Exception
     */
    public void logout() throws Exception {
        String url = getUrl(ACTION_LOGOUT);
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
            cookieStore.clear();
            this.JSESSIONID = null;

            httpclient.close();
        } catch (Exception e) {
            System.err.println("Error while logging out. Reason: ");
            e.printStackTrace();
        }
    }


    /**
     * Parse Cookie Information to extract JSESSIONID
     * @param headerValue
     * @return
     */
    private BasicClientCookie parseCookieInformation(String headerValue) {
        // Inspired by http://www.java2s.com/Code/Java/Network-Protocol/GettingtheCookiesfromanHTTPConnection.htm
        System.out.println("headerValue: " + headerValue);
        String domain = "";
        String path = "";
        boolean secure = false;
        String[] fields = headerValue.split(";\\s*");
        String jsessionid =  fields[0];
        for (int j = 1; j < fields.length; j++) {
            if ("secure".equalsIgnoreCase(fields[j].trim())) {
                secure = true;
            } else if (fields[j].indexOf('=') > 0) {
                String[] f = fields[j].split("=");
                if ("domain".equalsIgnoreCase(f[0].trim())) {
                    domain = f[1];
                } else if ("path".equalsIgnoreCase(f[0].trim())) {
                    path = f[1];
                }
            }
        }
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID",jsessionid);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setSecure(secure);
        this.JSESSIONID = jsessionid;
        return cookie;
    }

    /**
     * Are we logged in?
     * @return
     * @throws IOException
     */
    public Status getStatus() throws IOException {
        String url = getUrl(ACTION_STATUS);
        String result = httpGET(url);
        return GsonCreator.create().fromJson(result,Status.class);
    }

    /**
     * Get Collections
     * @return
     * @throws IOException
     */
    public List<Collection> getCollections() throws IOException {
        String url = getUrl(ACTION_COLLECTION);
        String result = httpGET(url);
        Collection[] myCollections = GsonCreator.create().fromJson(result,Collection[].class);
        return Arrays.asList(myCollections);
    }

    /**
     * Get Items in Collection
     * @param collection
     * @return
     * @throws IOException
     */
    public List<Item> getItemsByCollection(Collection collection) throws IOException {
        String url = String.format("%s/%s/items",getUrl(ACTION_COLLECTION),collection.getUuid());
        String result = httpGET(url);
        Item[] myItems = GsonCreator.create().fromJson(result,Item[].class);
        return Arrays.asList(myItems);
    }

    /**
     * Get all Communities
     * @return
     * @throws IOException
     */
    public List<Community> getCommunities() throws IOException {
        String url = getUrl(ACTION_COMMUNITIES);
        String result = httpGET(url);
        Community[] myCommunities = GsonCreator.create().fromJson(result,Community[].class);
        return Arrays.asList(myCommunities);
    }

    /**
     * Query DSpace (exact match) using key:value
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public List<Item> findItemByMetadata(String key,String value) throws Exception {
        MetadataEntry entry = new MetadataEntry();
        entry.setKey(key);
        entry.setValue(value);
        String json = gson().toJson(entry);
        String url = getUrl(ACTION_FIND_BY_METADATA_FIELD);
        String result = httpPOST(url,ACCEPT_HEADER,json);
        Item[] myItems = GsonCreator.create().fromJson(result,Item[].class);
        return Arrays.asList(myItems);
    }

    /**
     * Verify DSpace REST API is operational
     * @return
     * @throws IOException
     */
    public boolean isRestAvailable() throws IOException {
        String url = getUrl(ACTION_TEST_REST);
        String result = httpGET(url);
        return result.contains("REST api is running");
    }

    /**
     * Get Metadata from Item
     * @param item
     * @return
     * @throws IOException
     */
    public List<MetadataEntry> getItemMetadata(Item item) throws IOException {
        String url = String.format("%s/%s/metadata",getUrl(ACTION_ITEMS),item.getUuid());
        String result = httpGET(url);
        MetadataEntry[] entries = GsonCreator.create().fromJson(result,MetadataEntry[].class);
        return Arrays.asList(entries);
    }

    /**
     * Delete Metadata on Item
     * @param item 
     * @throws IOException
     */
    public boolean removeItemMetadata(Item item) throws IOException {
        String url = String.format("%s/%s/metadata",getUrl(ACTION_ITEMS),item.getUuid());
        boolean deleted = httpDelete(url);
        return deleted;
    }

    /**
     * Add Item with Metadata
     * @param item
     * @param collection
     * @return
     * @throws IOException
     */
    public Item addItem(Item item,Collection collection) throws Exception{
        String url = String.format("%s/%s/items",getUrl(ACTION_COLLECTION),collection.getUuid());
        String json = gson().toJson(item);
        String result = httpPOST(url,ACCEPT_HEADER,json);
        Item addedItem = GsonCreator.create().fromJson(result,Item.class);
        return addedItem;
    }

    /**
     * Add metadata entries to item
     * @param item
     * @param entries
     * @return
     * @throws Exception
     */
    public boolean addItemMetadata(Item item, List<MetadataEntry> entries) throws Exception {
        /* convert MetadataEntries to JSON */
        String url = String.format("%s/%s/metadata",getUrl(ACTION_ITEMS),item.getUuid());
        String json = gson().toJson(entries);
        String result = httpPOST(url,ACCEPT_HEADER,json);
        return true;
    }

    /**
     * Update metadata entries to item. Only entries changed needed
     * @param item
     * @param entries
     * @return
     * @throws Exception
     */
    public boolean updateItemMetadata(Item item, List<MetadataEntry> entries) throws Exception {
        String url = String.format("%s/%s/metadata",getUrl(ACTION_ITEMS),item.getUuid());
        String json = gson().toJson(entries);
        String result = httpPUT(url, ACCEPT_HEADER, json);
        return true;
    }

    /**
     * Delete Item from Collection
     * @param item
     * @param collection
     * @return
     * @throws IOException
     */
    public boolean deleteItem(Item item, Collection collection) throws IOException {
        /* Remove Item from Collection */
        System.out.println("Action::DeleteItem");
        // collections/:ID/items/:ID
        String url = String.format("%s/%s",getUrl(ACTION_ITEMS),item.getUuid());
        //String url = String.format("%s/collections/%s/%s",getUrl(ACTION_COLLECTION),collection.getUUID(),item.getUuid());
        boolean deleted = httpDelete(url);
        return deleted;
    }

    /* Not supported until DSpace v7.x
    public Item updateItem(Item item, Collection collection) {
        String url = String.format("%s/%s/items",getUrl(ACTION_COLLECTION),collection.getUuid());
        String json = gson().toJson(item);
        String result = httpPut(url,ACCEPT_HEADER,json);
        Item updatedItem = GsonCreator.create().fromJson(result,Item.class);
        return updatedItem;
    }

     */

    /**
     * Get List of Bitstreams from Item
     * @param item
     * @return
     * @throws IOException
     */
    public List<Bitstream> getBitstreams(Item item) throws IOException{
        String url =String.format("%s/%s/%s",getUrl(ACTION_ITEMS),item.getUuid(),"bitstreams");
        String result = httpGET(url);
        Bitstream[] bitstreams = GsonCreator.create().fromJson(result,Bitstream[].class);
        return Arrays.asList(bitstreams);
    }

    /**
     * Add Bitstream (File) to Item
     * @param file
     * @param bitstream
     * @param item
     * @return
     * @throws Exception
     */
    public Bitstream createBitstream(File file,Bitstream bitstream, Item item) throws Exception{
        String url = String.format("%s/%s/%s",getUrl(ACTION_ITEMS),item.getUuid(),"bitstreams");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        /* Create a bitstream form entity object */
        url = url.concat("?name="+file.getName());
        url = url.concat("&description=" + bitstream.getDescription());
        url = url.concat("&month=" + monthFormat.format(file.lastModified()));
        url = url.concat("&year=" + yearFormat.format(file.lastModified()));
        url = url.concat("&day=" + dayFormat.format(file.lastModified()));
        String result = httpPOSTFile(url,file);
        Bitstream createdBitstream = GsonCreator.create().fromJson(result,Bitstream.class);
        return createdBitstream;
    }

    /**
     * Delete Bitstream on Item
     * @param bitstream
     * @param item
     * @return
     * @throws IOException
     */
    public boolean deleteBitstream(Bitstream bitstream, Item item) throws IOException {
        String url = String.format("%s/%s/%s/%s",getUrl(ACTION_ITEMS),item.getUuid(),"bitstreams",bitstream.getUuid());
        boolean deleted = httpDelete(url);
        return deleted;
    }

}