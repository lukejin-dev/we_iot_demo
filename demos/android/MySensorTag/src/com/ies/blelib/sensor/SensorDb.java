package com.ies.blelib.sensor;

import java.util.HashMap;

public class SensorDb {

    public static HashMap<String, BleSensor> map = new HashMap<String, BleSensor>();
    
    static {
        map.put(TiHumiditySensor.UUID_SERVICE, new TiHumiditySensor());
        map.put(TiTemperatureSensor.UUID_SERVICE, new TiTemperatureSensor());
        map.put(TiAccelerometerSensor.UUID_SERVICE, new TiAccelerometerSensor());
        map.put(TiMagnetometerSensor.UUID_SERVICE, new TiMagnetometerSensor());
    }
    
    public static final BleSensor get(String uuid) {
        return map.get(uuid.toLowerCase());
    }
}
