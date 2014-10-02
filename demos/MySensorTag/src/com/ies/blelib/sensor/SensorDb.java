package com.ies.blelib.sensor;

import java.util.HashMap;

public class SensorDb {

    public static HashMap<String, BleSensor> map = new HashMap<String, BleSensor>();
    
    static {
        map.put(TiHumiditySensor.UUID_SERVICE, new TiHumiditySensor());
    }
    
    public static final BleSensor get(String uuid) {
        return map.get(uuid.toLowerCase());
    }
}
