package com.ies.blelib.service;

import java.util.HashMap;

public class GattServiceInfo {

    private String name_;
    private String type_;
    private String uuid_;
    
    public GattServiceInfo(String name, String type, String uuid) {
        name_ = name;
        type_ = type;
        uuid_ = uuid.toLowerCase();
    }
    
    public String get_name() {
        return name_;
    }
    
    public String get_type() {
        return type_;
    }
    
    public String get_uuid() {
        return uuid_;
    }
}
