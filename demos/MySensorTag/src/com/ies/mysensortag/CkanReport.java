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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ckan.CKANException;

import com.google.gson.Gson;

import android.os.Handler;
import android.util.Log;

public class CkanReport {

    private final static String TAG_ = 
            CkanReport.class.getSimpleName();
    
    private final static String DEFAULT_SERVER_ADDRESS = "202.121.178.242";
    private final static int    DEFAULT_PORT = 80;
    private final static String URL_PATH_ACTION = "/api/3/action/";
    private final static String DEFAULT_API_KEY = "268016bf-92cd-48ca-8406-3ad2f1528c1b";
    private final static String DEFAULT_RESOURCE_ID = "519e34eb-920d-4215-a634-a47832e03cf6";
    
    private Date last_report_time_;
    private boolean is_transferring_;
    private String server_address_;
    private String apikey_;
    private int port_;
    private Handler post_handler_;
    private String post_data_;
    private String resource_id_;
    
    public CkanReport() {
        this(DEFAULT_SERVER_ADDRESS, DEFAULT_PORT, DEFAULT_API_KEY, DEFAULT_RESOURCE_ID);
    }
    
    public CkanReport(String url, int port, String apikey, String resource_id) {
        server_address_ = url;
        port_ = port;
        apikey_ = apikey;
        is_transferring_ = false;
        last_report_time_ = new Date();   
        post_handler_ = new Handler();
        post_data_ = null;
        resource_id_ = resource_id;
    }
    
    private URL get_url(String path) {
        URL url;
        try {
            url = new URL("http://" + server_address_ + path);
        } catch ( MalformedURLException mue ) {
            System.err.println(mue);
            return null;
        }
        return url;
    }
    
    private URL get_action_url(String action) {
        return get_url(URL_PATH_ACTION + action);
    }
    
    private URL get_datastore_upsert_url() {
        return get_action_url("datastore_upsert");
    }
 
    protected String post(URL url, String data) {
        String body = "";
        
        Log.i(TAG_, "api key: " + this.apikey_);
        Log.i(TAG_, "url - " + url.toString() + "  data - " + data);
        
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost postRequest = new HttpPost(url.toString());
            postRequest.setHeader("X-CKAN-API-Key", this.apikey_);
            //postRequest.addHeader("Authorization", this.apikey_);
            //postRequest.setHeader("Authorization", this.apikey_);

            StringEntity input = new StringEntity(data);
            input.setContentType("application/json");
            postRequest.setEntity(input);

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
            // TODO Auto-generated catch block
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
        
        Gson gson = new Gson();
        
        SensorValueRecord r = new SensorValueRecord();
        r.set_mac(mac);
        r.set_sensor_uuid(id);
        r.set_value(value);
        
        List<SensorValueRecord> records = new ArrayList<SensorValueRecord>();
        records.add(r);
        String records_json_str = gson.toJson(records);
        Log.i(TAG_, "records json string: " + records_json_str);
        
        CkanDataStoreUpsertParam param = new CkanDataStoreUpsertParam();
        param.set_records(records_json_str);
        param.set_resource_id(resource_id_);
        
        post_data_ = gson.toJson(param);
        
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
    
    public class CkanDataStoreUpsertParam {
        public String resource_id;
        public boolean force;
        public String method;
        public String records;
        
        public CkanDataStoreUpsertParam() {
            force = true;
            method = "insert";
        }
        
        public void set_records(String r) {
            records = r;
        }
        
        public void set_resource_id(String id) {
            resource_id = id;
        }
    }
    
    public class SensorValueRecord {
        public String mac_address;
        public String sensor_uuid;
        public String date;
        public String value;
        
        public SensorValueRecord() {
            Date now = new Date();
            date = now.toString();
        }
        
        public void set_mac(String mac) {
            mac_address = mac;
        }
        
        public void set_sensor_uuid(String id) {
            sensor_uuid = id;
        }
        
        public void set_value(String v) {
            value = v;
        }
    }
    
    public class ReportThread extends Thread {
        public void run() {
            is_transferring_ = true;
            Log.i(TAG_, "start post...");
            post(get_datastore_upsert_url(), post_data_);
            Log.i(TAG_, "End post...");
            is_transferring_ = false;
        }
    }
}
