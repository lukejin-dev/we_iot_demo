package com.ies.blelib.sensor;

import android.bluetooth.BluetoothGattCharacteristic;

import com.google.gson.Gson;
import com.ies.blelib.service.*;


public class TiHumiditySensor extends TiSensor<Float> {

    public static final String UUID_SERVICE = 
            "f000aa20-0451-4000-b000-000000000000";
    public final String UUID_DATA = 
            "f000aa21-0451-4000-b000-000000000000";
    public final String UUID_CONFIGURATION = 
            "f000aa22-0451-4000-b000-000000000000";
    public final String UUID_PERIOD = 
            "f000aa23-0451-4000-b000-000000000000";
    
    @Override
    public String get_name() {
        return "Humidity";
    }

    @Override
    public String get_service_uuid() {
        return UUID_SERVICE;
    }

    @Override
    public String get_data_uuid() {
        return UUID_DATA;
    }

    @Override
    public String get_configure_uuid() {
        return UUID_CONFIGURATION;
    }

    @Override
    public Float parse(BluetoothGattCharacteristic c) {
        int a = shortUnsignedAtOffset(c, 2);
        // bits [1..0] are status bits and need to be cleared according
        // to the userguide, but the iOS code doesn't bother. It should
        // have minimal impact.
        a = a - (a % 4);
        return (-6f) + 125f * (a / 65535f);
    }
    
    public String get_value_string() {
       return "" + get_value();
    }
}
