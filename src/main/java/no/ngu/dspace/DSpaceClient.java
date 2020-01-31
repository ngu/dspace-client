package no.ngu.dspace;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import no.ngu.dspace.model.*;
import no.ngu.dspace.utils.GsonCreator;

import org.apache.commons.io.IOUtils;

/**
 * https://wiki.lyrasis.org/display/DSDOC6x/REST+API
 */
public class DSpaceClient {

    private final String urlPrefix;
    private final String username;
    private final String password;

    private final String ACTION_LOGIN = "login";
    private final String ACTION_STATUS = "status";
    private final String ACTION_COLLECTION = "collections";
    private final String ACTION_COMMUNITIES = "communities";
    private final String ACTION_ITEMS = "items";
    private final String ACTION_FIND_BY_METADATA_FIELD = "items/find-by-metadata-field";

    // Constructor
    public DSpaceClient(String urlPrefix, String username, String password) {
        this.urlPrefix = urlPrefix;
        this.username = username;
        this.password = password;
    }

    private Gson gson() {
        GsonBuilder b = GsonCreator.builder();
        return b.create();
    }

    private String getUrl(String action) {
        return String.format("%s/%s",this.urlPrefix,action);
    }
    private String httpGET(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        //conn.setRequestProperty("Authorization", "fmetoken token=" + getToken());
        if (conn.getResponseCode() != 200) {
            throw new IOException(
                    "GET got " + conn.getResponseCode() + " " + conn.getResponseMessage() + " from " + url);
        }
        String s = IOUtils.toString(conn.getInputStream());
        System.out.println(url + " : ");
        System.out.println(s);
        return s;
    }
    private String httpPOST(String url, String contentType, String data) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        //conn.setRequestProperty("Authorization", "fmetoken token=" + getToken());
        conn.setRequestMethod("POST");

        if (data != null) {
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", contentType);
            conn.getOutputStream().write(data.getBytes("UTF-8"));
        }

        if (conn.getResponseCode() != 200) {
            throw new IOException(
                    "POST got " + conn.getResponseCode() + " " + conn.getResponseMessage() + " from " + url);
        }
        String s = IOUtils.toString(conn.getInputStream());
        System.out.println(url + " : ");
        System.out.println(s);
        return s;
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
        // check if request is 200, and not 4XX or 5XX
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
        //List<Collection> collections = GsonCreator.create().fromJson(result,List<Collection.class>);
        return (List<Collection>) GsonCreator.create().fromJson(result,Collection.class);
    }

    public List<Community> getCommunities() throws IOException {
        String url = getUrl(ACTION_COMMUNITIES);
        String result = httpGET(url);
        return GsonCreator.create().fromJson(result,List<Community.class>);
    }

    public List<Item> findItemByMetadata(String key,String value) {
        // HttpPost
        // {"key":"dc.title","value":"Test"}
        /* Need to query localId to find our own data!? */
        List<Item> items = new ArrayList<Item>();
        return items;
    }

    public List<Item> getItemsByCommunitiy(Community community) {
        List<Item> items = new ArrayList<Item>();
        return items;
    }

    public void addMetadata(int itemId, List<MetadataEntry> entries) {
        /* convert MetadataEntries to JSON */
        String json = gson().toJson(entries);
    }

}