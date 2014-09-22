package com.ies.blelib;

import java.util.Date;

import com.ies.mysensortag.DeviceScanActivity;

import android.text.format.Time;
import android.util.Log;

public class BeaconScanInfo {
    
    private final static String TAG_ = BeaconScanInfo.class.getSimpleName();
    
    private String name_;
    private String address_;
    private int    rssi_;
    Date  last_update_time_;
    
    public BeaconScanInfo(String name, String address, int rssi) {
        name_ = name;
        address_ = address;
        set_rssi(rssi);
    }
    
    public void set_rssi(int rssi) {
        rssi_ = rssi;
        last_update_time_ = new Date();
    }
    
    public int get_rssi() {
        return rssi_;
    }
    
    public String get_address() {
        return address_;
    }
    
    public String get_name() {
        return name_;
    }
    
    public boolean is_expired() {
        Date now = new Date();
        long diff_seconds = (now.getTime() - last_update_time_.getTime()) / 1000;
        Log.d(TAG_, "time diff: " + diff_seconds);
        if (diff_seconds > 1) {
            return true;
        }
        return false;
    }
}
