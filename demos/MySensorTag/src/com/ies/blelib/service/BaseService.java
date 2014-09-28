package com.ies.blelib.service;

public abstract class BaseService {
    
    public abstract String getUUID();

    public String getName() {
        return "unknown";
    }

    public abstract String getCharacteristicName(String uuid);
}
