package ies.iot.demolib.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DemoSettings {
    private static DemoSettings mInstance;
    
    private final String SETTING_FILE = "IoTDemoSettings";
    private final String KEY_DEVICE_NAME = "BleDeviceName";
    private final String KEY_DEVICE_ADDRESS = "BleDeviceAddress";
    
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
            return null;
        }
        return temp;
    }
    
    public void setDeviceName(Context context, String name) {
        String temp = (name == null) ? "" : name;
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SETTING_FILE, Activity.MODE_MULTI_PROCESS).edit();
        editor.putString(KEY_DEVICE_NAME, temp);
    }

    public String getDeviceAddress(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                SETTING_FILE, Activity.MODE_MULTI_PROCESS);
        
        String temp = pref.getString(KEY_DEVICE_ADDRESS, null);
        if (temp != null && temp.length() == 0) {
            return null;
        }
        return temp;
    }
    
    public void setDeviceAddress(Context context, String addr) {
        String temp = (addr == null) ? "" : addr;
        
        SharedPreferences.Editor editor = context.getSharedPreferences(
                SETTING_FILE, Activity.MODE_MULTI_PROCESS).edit();
        editor.putString(KEY_DEVICE_ADDRESS, temp);
    }
    
}
