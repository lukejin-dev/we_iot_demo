package com.ies.blelib.sensor;

public abstract class Sensor {
    private final static String TAG_ = Sensor.class.getSimpleName();
    
    public abstract String get_name();
    public abstract String get_service_uuid();
    public abstract String get_data_uuid();
    public abstract String get_config_uuid();
}
