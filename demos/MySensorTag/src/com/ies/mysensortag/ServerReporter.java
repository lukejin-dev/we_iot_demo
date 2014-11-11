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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.google.gson.Gson;
import com.ies.blelib.sensor.TiAccelerometerSensor;
import com.ies.blelib.sensor.TiHumiditySensor;
import com.ies.blelib.sensor.TiMagnetometerSensor;
import com.ies.blelib.sensor.TiSensor;
import com.ies.blelib.sensor.TiTemperatureSensor;

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
    private List<SensorValues> value_list_;
    private int server_errors_;
    
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
        server_errors_ = 0;
        value_list_ = new ArrayList<SensorValues>();
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
       
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        HttpConnectionParams.setSoTimeout(params, 3000);            
        HttpClient httpclient = new DefaultHttpClient(params);
        
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
    
    public void report_sensor_data(TiSensor sensor, String mac, String id) {
        try {
            SensorValues sensor_values = new SensorValues();
            if (sensor instanceof TiAccelerometerSensor) {
                float[] data = (float[])sensor.get_value();
                sensor_values.a_x = Float.toString(data[0]);
                sensor_values.a_y = Float.toString(data[1]);
                sensor_values.a_z = Float.toString(data[2]);
            } else if (sensor instanceof TiHumiditySensor) {
                Float data = (Float)sensor.get_value();
                sensor_values.humidity = Float.toString(data);
            } else if (sensor instanceof TiMagnetometerSensor) {
                float[] data = (float[])sensor.get_value();
                sensor_values.m_x = Float.toString(data[0]);
                sensor_values.m_y = Float.toString(data[1]);
                sensor_values.m_z = Float.toString(data[2]);            
            } else if (sensor instanceof TiTemperatureSensor) {
                float[] data = (float[])sensor.get_value();
                sensor_values.ambient = Float.toString(data[0]);
                sensor_values.target = Float.toString(data[1]);
            } else {
                Log.e(TAG_, "Unknown sensor type");
            }
            value_list_.add(sensor_values);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        if (is_too_fast() || is_transferring_) {
            return;
        }
        
        mac_ = mac;
        Gson gson = new Gson();
        
        post_data_ = gson.toJson(value_list_);;
        
        ReportThread report_thread = new ReportThread();
        report_thread.start();
        
        
    }
    
    private boolean is_too_fast() {
        Date now = new Date();
        long diff_seconds = 
                (now.getTime() - last_report_time_.getTime());
        Log.v(TAG_, "diff seconds: " + diff_seconds);
        if (diff_seconds < 500) {
            Log.w(TAG_, "too fast");
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
            
            value_list_.clear();
            last_report_time_ = new Date();
            is_transferring_ = false;
        }
    }
    
    class SensorValues {
        public String a_x;
        public String a_y;
        public String a_z;
        
        public String humidity;
        
        public String m_x;
        public String m_y;
        public String m_z;
        
        public String ambient;
        public String target;        
    }
}
