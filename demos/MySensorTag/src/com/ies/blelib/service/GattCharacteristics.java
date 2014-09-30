package com.ies.blelib.service;

public class GattCharacteristics {
    private String name_;
    private String type_;
    private String uuid_;
    
    public GattCharacteristics(String name, String type, String uuid) {
        name_ = name;
        type_ = type;
        uuid_ = uuid.toLowerCase();
    }
    
    public String get_name() {
        return name_;
    }
    
    public String get_uuid() {
        return uuid_;
    }
    
    public String get_type() {
        return type_;
    }
}
