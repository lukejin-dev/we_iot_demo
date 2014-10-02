package com.ies.blelib.sensor;

import java.util.HashMap;

public class SensorDb {

    public static HashMap<String, Sensor> map = new HashMap<String, Sensor>();
    
    public static final Sensor get(String uuid) {
        return map.get(uuid.toLowerCase());
    }
}
