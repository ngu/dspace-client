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
import org.apache.http.HttpResponse;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * https://wiki.lyrasis.org/display/DSDOC6x/REST+API
 */
public class DSpaceClient {

    private final String urlPrefix;
    private final String username;
    private final String password;

    private final String ACCEPT_HEADER = "application/json";

    private final String ACTION_TEST_REST = "test";
    private final String ACTION_LOGIN = "login";
    private final String ACTION_STATUS = "status";
    private final String ACTION_COLLECTION = "collections";
    private final String ACTION_COMMUNITIES = "communities";
    private final String ACTION_ITEMS = "items";
    private final String ACTION_FIND_BY_METADATA_FIELD = "items/find-by-metadata-field";
    //private final CookieStore cookieStore = new BasicCookieStore();
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

    // Json-util
    private Gson gson() {
        GsonBuilder b = GsonCreator.builder();
        return b.create();
    }

    /* Helper method generating correct REST endpoint URLs  */
    private String getUrl(String action) {
        return String.format("%s/%s",this.urlPrefix,action);
    }

    private String httpGET(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("Accept", ACCEPT_HEADER);
        if (JSESSIONID!= null && !JSESSIONID.isEmpty()) {
            //System.out.println("Reusing cookie: "+JSESSIONID);
            conn.addRequestProperty("Cookie",JSESSIONID);
        }

        if (conn.getResponseCode() != 200) {
            throw new IOException(
                    "GET got " + conn.getResponseCode() + " " + conn.getResponseMessage() + " from " + url);
        }
        String s = IOUtils.toString(conn.getInputStream());
        return s;
    }

    private String httpPOSTFile(String url, File file) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // build entity and upload the file
        FileEntity postData = new FileEntity(file,ContentType.create(getFiletype(file)));
        //HttpEntity postData = MultipartEntityBuilder.create().addBinaryBody("upfile", file).build();
        HttpUriRequest postRequest = RequestBuilder.post(url).setEntity(postData).build();

        //HttpPost httppost = new HttpPost(url);
        if (JSESSIONID != null) {
            postRequest.addHeader("Cookie",JSESSIONID);
            System.out.println("JSESSSIONID Cookie: " + JSESSIONID);
        } else {
            System.err.println("No JSESSSIONID Cookie");
        }
        postRequest.addHeader("Accept", ACCEPT_HEADER);
        System.out.println("Executing request " + postRequest.getRequestLine());
        HttpResponse response = httpclient.execute(postRequest);
        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
        System.out.println("HttpPost:MultiPartFile: " + response ) ;
        //Throw runtime exception if status code isn't 200
        if (response.getStatusLine().getStatusCode() == 401) {
            System.err.println("Not authorized!!!");
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }
        //Create the StringBuffer object and store the response into it.
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        httpclient.close();
        return String.valueOf(result);
    }

    private String getFiletype(File file) {
        String filename = file.getName().toLowerCase();
        String result = null;
        if (filename.endsWith(".pdf")) {
            result = String.format("application/%s", "pdf");
        } else if (filename.endsWith(".zip")) {
            result =  String.format("application/%s", "pdf");
        } else if (filename.endsWith(".png")) {
            result = String.format("image/%s", "pdf");
        }
        return result;
    }

    private String httpPOST(String url, String contentType, String data) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        if (JSESSIONID != null) {
            conn.addRequestProperty("Cookie",JSESSIONID);
        }
        conn.setRequestMethod("POST");
        conn.setRequestProperty("accept", ACCEPT_HEADER);
        if (data != null) {
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", contentType);
            conn.getOutputStream().write(data.getBytes("UTF-8"));
        }

        if (conn.getResponseCode() != 200) {
            throw new IOException(
                    "POST got " + conn.getResponseCode() + " " + conn.getResponseMessage() + " from " + url);
        }
        Set<String> fields = conn.getHeaderFields().keySet();

        for (String field: fields) {
            if (field != null && field.equalsIgnoreCase("Set-Cookie")) {
                BasicClientCookie cookie = parseCookieInformation(conn.getHeaderField(field));
                cookieStore.addCookie(cookie);
                this.JSESSIONID = cookie.getValue();
            }

        }
        String s = IOUtils.toString(conn.getInputStream());
        return s;
    }

    /**
     * HTTP Delete - To be used carefully!
     * @param url
     * @return
     * @throws IOException
     */
    private boolean httpDelete(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        if (JSESSIONID != null) {
            conn.addRequestProperty("Cookie",JSESSIONID);
        }
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("accept", ACCEPT_HEADER);
        if (conn.getResponseCode() != 200) {
            throw new IOException(
                    "DELETE got " + conn.getResponseCode() + " " + conn.getResponseMessage() + " from " + url);
        }
        return true;
    }

    private BasicClientCookie parseCookieInformation(String headerValue) {
        String domain = "";
        String path = "";
        boolean secure = false;
        String[] fields = headerValue.split(";\\s*");
        String jsessionid =  fields[0];
        for (int j = 1; j < fields.length; j++) {
            if ("secure".equalsIgnoreCase(fields[j].trim())) {
                System.out.println("secure=true");
                secure = true;
            } else if (fields[j].indexOf('=') > 0) {
                String[] f = fields[j].split("=");
                if ("expires".equalsIgnoreCase(f[0].trim())) {
                    System.out.println("expires"+ f[1]);
                } else if ("domain".equalsIgnoreCase(f[0].trim())) {
                    System.out.println("domain"+ f[1]);
                    domain = f[1];
                } else if ("path".equalsIgnoreCase(f[0].trim())) {
                    System.out.println("path"+ f[1]);
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

    public boolean login() throws Exception {
        String url = getUrl(ACTION_LOGIN);
        Map<String, String> postParameters = new HashMap<>();
        postParameters.put("email",this.username);
        postParameters.put("password",this.password);
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String, String> e : postParameters.entrySet()) {
            if (postData.length() > 0) {
                postData.append('&');
            }
            postData.append(e.getKey());
            postData.append('=');
            postData.append(URLEncoder.encode(e.getValue(), "UTF-8"));
        }
        String result = httpPOST(url, "application/x-www-form-urlencoded", postData.toString());
        System.out.println("Action::Login" + result);
        return true;
    }

    public Status getStatus() throws IOException {
        String url = getUrl(ACTION_STATUS);
        String result = httpGET(url);
        return GsonCreator.create().fromJson(result,Status.class);
    }

    public List<Collection> getCollections() throws IOException {
        String url = getUrl(ACTION_COLLECTION);
        String result = httpGET(url);
        Collection[] myCollections = GsonCreator.create().fromJson(result,Collection[].class);
        return Arrays.asList(myCollections);
    }
    public List<Item> getItemsByCollection(Collection collection) throws IOException {
        String url = String.format("%s/%s/items",getUrl(ACTION_COLLECTION),collection.getUUID());
        String result = httpGET(url);
        Item[] myItems = GsonCreator.create().fromJson(result,Item[].class);
        return Arrays.asList(myItems);
    }

    public List<Community> getCommunities() throws IOException {
        String url = getUrl(ACTION_COMMUNITIES);
        String result = httpGET(url);
        // Rewrite? https://stackoverflow.com/questions/5554217/google-gson-deserialize-listclass-object-generic-type/5554296#5554296
        Community[] myCommunities = GsonCreator.create().fromJson(result,Community[].class);
        return Arrays.asList(myCommunities);
    }

    public List<Item> findItemByMetadata(String key,String value) throws Exception {
        // HttpPost
        // {"key":"dc.title","value":"Test"}
        /* Need to query localid to find our own data!? */
        MetadataEntry entry = new MetadataEntry();
        entry.setKey(key);
        entry.setValue(value);
        String json = gson().toJson(entry);
        String url = getUrl(ACTION_FIND_BY_METADATA_FIELD);
        String result = httpPOST(url,ACCEPT_HEADER,json);
        Item[] myItems = GsonCreator.create().fromJson(result,Item[].class);
        return Arrays.asList(myItems);
    }

    public boolean isRestAvailable() throws IOException {
        String url = getUrl(ACTION_TEST_REST);
        String result = httpGET(url);
        return result.contains("REST api is running");
    }

    public List<MetadataEntry> getItemMetadata(Item item) throws IOException {
        System.out.println("Action::GetItem::Metadata");
        String url = String.format("%s/%s/metadata",getUrl(ACTION_ITEMS),item.getUuid());
        String result = httpGET(url);
        System.out.println(result);
        MetadataEntry[] entries = GsonCreator.create().fromJson(result,MetadataEntry[].class);
        return Arrays.asList(entries);
    }

    public Item addItem(Item item,Collection collection) throws IOException{
        String url = String.format("%s/%s/items",getUrl(ACTION_COLLECTION),collection.getUUID());
        String json = gson().toJson(item);
        String result = httpPOST(url,ACCEPT_HEADER,json);
        Item addedItem = GsonCreator.create().fromJson(result,Item.class);
        return addedItem;
    }

    public boolean addItemMetadata(Item item, List<MetadataEntry> entries) throws Exception {
        /* convert MetadataEntries to JSON */
        System.out.println("Action::AddMetadataToItem");
        String url = String.format("%s/%s/metadata",getUrl(ACTION_ITEMS),item.getUuid());
        String json = gson().toJson(entries);
        String result = httpPOST(url,ACCEPT_HEADER,json);
        return true;
    }

    public boolean deleteItem(Item item, Collection collection) throws IOException {
        /* Remove Item from Collection */
        System.out.println("Action::DeleteItem");
        // collections/:ID/items/:ID
        String url = String.format("%s/%s",getUrl(ACTION_ITEMS),item.getUuid());
        //String url = String.format("%s/collections/%s/%s",getUrl(ACTION_COLLECTION),collection.getUUID(),item.getUuid());
        boolean deleted = httpDelete(url);
        System.out.println("Result httpDelete: " + deleted);
        return deleted;
    }

    public List<Bitstream> getBitstreams(Item item) throws IOException{
        String url =String.format("%s/%s/%s",getUrl(ACTION_ITEMS),item.getUuid(),"bitstreams");
        String result = httpGET(url);
        Bitstream[] bitstreams = GsonCreator.create().fromJson(result,Bitstream[].class);
        return Arrays.asList(bitstreams);
    }


    public Bitstream createBitstream(File file,Bitstream bitstream, Item item) throws Exception{
        //@Path("/{item_id}/bitstreams")
        //public Bitstream addItemBitstream
        System.out.println("Action::CreateBitstream");
        String url = String.format("%s/%s/%s",getUrl(ACTION_ITEMS),item.getUuid(),"bitstreams");
        //System.out.println("HttpPost: " + url);
        /* Create a bitstream object */
        url = url.concat("?name="+file.getName());
        url = url.concat("&description=" + bitstream.getDescription());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        url = url.concat("&month=" + monthFormat.format(file.lastModified()));
        url = url.concat("&year=" + yearFormat.format(file.lastModified()));
        url = url.concat("&day=" + dayFormat.format(file.lastModified()));
        System.out.println("URL to POST: " + url);
        String result = httpPOSTFile(url,file);
        Bitstream createdBitstream = GsonCreator.create().fromJson(result,Bitstream.class);
        String uploadResult = httpPOSTFile(url,file);
        /* Upload a file corresponding to the created bitstream */
        System.out.println("Result: " + uploadResult);
        return createdBitstream;
    }
    public boolean deleteBitstream(Bitstream bitstream, Item item) throws IOException {
        System.out.println("Action::DeleteBitstream");
        //DELETE /items/{item id}/bitstreams/{bitstream id}
        String url = String.format("%s/%s/%s/%s",getUrl(ACTION_ITEMS),item.getUuid(),"bitstreams",bitstream.getUuid());
        boolean deleted = httpDelete(url);
        System.out.println("Result httpDelete: " + deleted);
        return deleted;
    }



}