package io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IO {

    /*
    public static void main(String[] args) {
    
        JSONObject json = new JSONObject();
        try {
            json.put("name", "jim");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        get(new HashMap<String,String>());
    }
    */

    private static final int TIMEOUT_MILLIS = 10000;

    // table: messages, users
    public synchronized static HttpResponse post(HashMap<String, String> data, String table) {
        try {
            JSONObject json = new JSONObject();
            for (String str : data.keySet()) {
                json.append(str, data.get(str));
            }
            HttpClient httpClient = HttpClientBuilder.create().build();
            // first entry is the table "messages," second
            HttpPost request = new HttpPost(
                    "http://michaelman.net/rest.php/" + table + "/1");
            
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT_MILLIS)
                    .setConnectTimeout(TIMEOUT_MILLIS)
                    .setConnectionRequestTimeout(TIMEOUT_MILLIS)
                    .build();
            request.setConfig(requestConfig);

            StringEntity params = new StringEntity(json.toString());
            System.out.println("POST: " + json.toString());
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.setEntity(params);
            
            HttpResponse response = httpClient.execute(request);
            request.releaseConnection();
            //System.out.println(readResponse(response));
            return response;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // calling /messages/{ID} gets a specific item, not including an ID gets ALL
    // posts
    public synchronized static JSONArray get(HashMap<String, String> params, String table) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        try {
            // first entry is the table "messages," second
            String url = "http://michaelman.net/rest.php/" + table + "/";
            boolean first = true;
            if (params != null) {
                for (String key : params.keySet()) {
                    url += (first ? "?" : "&") + key + "=" + params.get(key);
                    first = false;
                }
            }
            //System.out.println("GET: " + url);
            HttpGet request = new HttpGet(url);
            
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT_MILLIS)
                    .setConnectTimeout(TIMEOUT_MILLIS)
                    .setConnectionRequestTimeout(TIMEOUT_MILLIS)
                    .build();
            request.setConfig(requestConfig);

            HttpResponse response = httpClient.execute(request);
            String jsonStr = EntityUtils.toString(response.getEntity());
            //System.out.println("GET: " + jsonStr);
            JSONArray array = new JSONArray(jsonStr);
            request.releaseConnection();
            return array;
        } catch (Exception ex) {
            System.out.println(ex);
            return null;
        }
    }

    public static boolean hasName(String name){
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("name",name);
        JSONArray results = get(params,"users");
        
        
        return (results.length() != 0);
    }
    
    public static String readResponse(HttpResponse response) {
        try {
            BufferedReader r = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder total = new StringBuilder();

            String line = null;

            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            r.close();
            return total.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
