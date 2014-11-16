package ies.iot.demolib.utils;

import ies.iot.demolib.sensors.TiAccelerometerSensor;
import ies.iot.demolib.sensors.TiHumiditySensor;
import ies.iot.demolib.sensors.TiMagnetometerSensor;
import ies.iot.demolib.sensors.TiSensor;
import ies.iot.demolib.sensors.TiTemperatureSensor;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.google.gson.Gson;

import android.provider.Settings.Secure;
import android.util.Log;

public class ServerReporter3 {

    private final static String TAG_ = 
            ServerReporter3.class.getSimpleName();
    
    private final static String DEFAULT_SERVER_ADDRESS = 
            "http://69.10.52.93:8400/api/p3/write";
    
    private Date last_report_time_;
    private boolean is_transferring_;
    private String server_address_;
    private String post_data_;
    private List<RssiValues> value_list_;
    private int server_errors_;
    private int report_interval_;
    private static final Object itemLock = new Object();
    private String android_id_;
    
    public ServerReporter3() {
    }
    
    public ServerReporter3(String url, String android_id) {
        server_address_ = url;
        is_transferring_ = false;
        last_report_time_ = new Date();   
        post_data_ = null;
        server_errors_ = 0;
        value_list_ = new ArrayList<RssiValues>();
        report_interval_ = 1000;
        android_id_ = android_id;         
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
    
    public void set_server_address(String address) {
        server_address_ = address;
    }
 
    protected String post(URL url, String data) {
        String body = "";
        
        Log.i(TAG_, "url - " + url.toString() + "\n" +
                "  stationid - " + android_id_ +
                "  data - " + data);
       
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 2000);
        HttpConnectionParams.setSoTimeout(params, 2000);            
        HttpClient httpclient = new DefaultHttpClient(params);
        
        try {
            HttpPost postRequest = new HttpPost(url.toString());
            
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("stationid", android_id_));
            nameValuePairs.add(new BasicNameValuePair("content", data));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
            Log.v(TAG_, "=====> " + entity.toString());
            postRequest.setEntity(entity);
            
            HttpResponse response = httpclient.execute(postRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (response.getEntity().getContent())));
            String line = "";
            while ((line = br.readLine()) != null) {
                body += line;
            }
            Log.i(TAG_, "response: " + body);
            
            server_errors_ = 0;
        } catch (ClientProtocolException cpe) {
            Log.e(TAG_, "ClientProtocolException:" + cpe.toString());
            cpe.printStackTrace();
            server_errors_ ++;
        } catch (HttpHostConnectException hhce) {
            Log.e(TAG_, "HttpHostConnectException:" + hhce.toString());
            hhce.printStackTrace();
            server_errors_ ++;
        } catch (IOException e) {
            Log.e(TAG_, "HttpHostConnectException:" + e.toString());
            e.printStackTrace();
            server_errors_ ++;
        } catch (Exception e) { 
            e.printStackTrace();
            server_errors_ ++;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        
        Log.v(TAG_, "post done.");
        return body;
    }

    public int get_server_errors() {
        return server_errors_;
    }
    
    public void set_report_interval(int interval) {
        Log.v(TAG_, "report interval: " + interval);
        report_interval_ = interval;
    }
    
    public RssiValues getExistingItem(String mac) {
        for (RssiValues item:value_list_) {
            if (mac.equalsIgnoreCase(item.clientid)) {
                return item;
            }
        }
        return null;
    }
    public void report_sensor_rssi(String mac, int rssi) {
        Log.v(TAG_, "server address: " + server_address_);
        if (server_address_ == null) {
            return;
        }
        
        synchronized(itemLock) {
            try {
                RssiValues rssi_value = getExistingItem(mac);
                if (rssi_value == null) {
                    rssi_value = new RssiValues();
                    rssi_value.clientid = mac;
                    rssi_value.stationid = android_id_;
                    value_list_.add(rssi_value);
                }
                rssi_value.rssi = rssi;
                
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            
            if (is_too_fast() || is_transferring_) {
                return;
            }
            
            Gson gson = new Gson();
            
            post_data_ = gson.toJson(value_list_);;
        }
        
        ReportThread report_thread = new ReportThread();
        report_thread.start();
        
        
    }
    
    private boolean is_too_fast() {
        Date now = new Date();
        long diff_seconds = 
                (now.getTime() - last_report_time_.getTime());
        //Log.v(TAG_, "diff seconds: " + diff_seconds);
        if (diff_seconds < report_interval_) {
            //Log.w(TAG_, "too fast");
            return true;
        }
        return false;
    }
    
    public class ReportThread extends Thread {
        public void run() {
            is_transferring_ = true;
            try {
                Log.i(TAG_, "start post...");
                post(get_url(), post_data_);
                Log.i(TAG_, "End post...");
            } catch (Exception e) {
                
            }
            
            synchronized(itemLock) {
                value_list_.clear();
                last_report_time_ = new Date();
                is_transferring_ = false;
            }
        }
    }
    
    class RssiValues {
        public int rssi;
        public String clientid;
        public String stationid;
    }
    
}
