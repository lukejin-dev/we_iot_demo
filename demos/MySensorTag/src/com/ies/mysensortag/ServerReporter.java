package com.ies.mysensortag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

import android.os.Handler;
import android.util.Log;

public class ServerReporter {

    private final static String TAG_ = 
            ServerReporter.class.getSimpleName();
    
    private final static String DEFAULT_SERVER_ADDRESS = 
            "http://192.168.88.2:83/api/p2/write";
    
    private Date last_report_time_;
    private boolean is_transferring_;
    private String server_address_;
    private String post_data_;
    private String mac_;
    
    public ServerReporter() {
        this(DEFAULT_SERVER_ADDRESS);
    }
    
    public ServerReporter(String url) {
        if (url == null || url.length() == 0) {
            server_address_ = DEFAULT_SERVER_ADDRESS;
        } else {
            server_address_ = url;
        }
        is_transferring_ = false;
        last_report_time_ = new Date();   
        post_data_ = null;
    }
    
    private URL get_url() {
        URL url;
        try {
            url = new URL(server_address_);
        } catch ( MalformedURLException mue ) {
            System.err.println(mue);
            return null;
        }
        return url;
    }
    
 
    protected String post(URL url, String data) {
        String body = "";
        
        Log.i(TAG_, "url - " + url.toString() + "\n" +
                "  clientid - " + mac_ +
                "  data - " + data);
        
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost postRequest = new HttpPost(url.toString());
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("clientid", mac_));
            nameValuePairs.add(new BasicNameValuePair("content", data));
            
            postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            
            HttpResponse response = httpclient.execute(postRequest);
            int statusCode = response.getStatusLine().getStatusCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (response.getEntity().getContent())));

            String line = "";
            while ((line = br.readLine()) != null) {
                body += line;
            }
            
            Log.i(TAG_, "response: " + body);
        } catch (ClientProtocolException cpe) {
            Log.e(TAG_, "ClientProtocolException:" + cpe.toString());
            cpe.printStackTrace();
        } catch (HttpHostConnectException hhce) {
            Log.e(TAG_, "HttpHostConnectException:" + hhce.toString());
            hhce.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG_, "HttpHostConnectException:" + e.toString());
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        
        return body;
    }

    
    public void report_sensor_data(String mac, String id, String value) {
        if (is_too_fast() || is_transferring_) {
            //
            // Just ignore if the sending interval is too fast.
            //
            return;
        }
        
        mac_ = mac;
        Gson gson = new Gson();
        
        post_data_ = value;
        
        ReportThread report_thread = new ReportThread();
        report_thread.start();
        last_report_time_ = new Date();
        
    }
    
    private boolean is_too_fast() {
        Date now = new Date();
        long diff_seconds = 
                (now.getTime() - last_report_time_.getTime()) / 1000;
        if (diff_seconds < 2) {
            return true;
        }
        return false;
    }
    
    public class ReportThread extends Thread {
        public void run() {
            is_transferring_ = true;
            Log.i(TAG_, "start post...");
            post(get_url(), post_data_);
            Log.i(TAG_, "End post...");
            is_transferring_ = false;
        }
    }
}
