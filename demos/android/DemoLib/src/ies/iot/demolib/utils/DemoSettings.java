package ies.iot.demolib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class DemoSettings {
    private final String TAG = getClass().getSimpleName();
    
    private static DemoSettings mInstance;
    
    private final String SETTING_FILE = "IoTDemoSettings";
    private final String KEY_DEVICE_NAME = "BleDeviceName";
    private final String KEY_DEVICE_ADDRESS = "BleDeviceAddress";
    private final String KEY_SERVER_URL = "report_server_url";
    
    public static DemoSettings getInstance() {
        if (mInstance == null) {
            mInstance = new DemoSettings();
        }
        return mInstance;
    }
    
    public String getDeviceName(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                SETTING_FILE, Activity.MODE_MULTI_PROCESS);
        String temp = pref.getString(KEY_DEVICE_NAME, null);
        if (temp != null && temp.length() == 0) {
            Log.v(TAG, "getDeviceName() : null");
            return null;
        }
        
        Log.v(TAG, "getDeviceName() : " + temp);
        return temp;
    }
    
    public void setDeviceName(Context context, String name) {
        String temp = (name == null) ? "" : name;
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SETTING_FILE, Activity.MODE_MULTI_PROCESS).edit();
        editor.putString(KEY_DEVICE_NAME, temp);
        editor.commit();
    }

    public String getDeviceAddress(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                SETTING_FILE, Activity.MODE_MULTI_PROCESS);
        
        String temp = pref.getString(KEY_DEVICE_ADDRESS, null);
        if (temp != null && temp.length() == 0) {
            Log.v(TAG, "getDeviceAddress() : null");
            return null;
        }
        Log.v(TAG, "getDeviceAddress() : " + temp);
        return temp;
    }
    
    public void setDeviceAddress(Context context, String addr) {
        String temp = (addr == null) ? "" : addr;
        
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SETTING_FILE, Activity.MODE_MULTI_PROCESS).edit();
        editor.putString(KEY_DEVICE_ADDRESS, temp);
        editor.commit();
    }
    
    public String getServerUrl(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                SETTING_FILE, Activity.MODE_MULTI_PROCESS);
        
        String temp = pref.getString(KEY_SERVER_URL, null);
        if (temp != null && temp.length() == 0) {
            Log.v(TAG, "getServerUrl() : null");
            return null;
        }
        Log.v(TAG, "getServerUrl() : " + temp);
        return temp;        
    }
    
    public void setServerUrl(Context context, String url) {
        String temp = (url == null) ? "" : url;
        
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SETTING_FILE, Activity.MODE_MULTI_PROCESS).edit();
        editor.putString(KEY_SERVER_URL, temp);
        editor.commit();
    }    
}
