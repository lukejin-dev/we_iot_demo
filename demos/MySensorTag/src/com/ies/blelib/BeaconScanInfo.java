package com.ies.blelib;

public class BeaconScanInfo {
    private String name_;
    private String address_;
    private int    rssi_;
    
    public BeaconScanInfo(String name, String address, int rssi) {
        name_ = name;
        address_ = address;
        rssi_ = rssi;
    }
    
    public void set_rssi(int rssi) {
        rssi_ = rssi;
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
}
