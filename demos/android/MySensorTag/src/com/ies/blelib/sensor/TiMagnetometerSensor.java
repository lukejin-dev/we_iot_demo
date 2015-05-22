package com.ies.blelib.sensor;

import com.google.gson.Gson;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public class TiMagnetometerSensor extends TiSensor<float[]> {
    public static final String UUID_SERVICE = "f000aa30-0451-4000-b000-000000000000";
    private static final String UUID_DATA = "f000aa31-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = "f000aa32-0451-4000-b000-000000000000";
    private static final String UUID_PERIOD = "f000aa33-0451-4000-b000-000000000000";
    
    private static final int PERIOD_MIN = 10;
    private static final int PERIOD_MAX = 255;

    private int period_ = PERIOD_MIN;
    
    @Override
    public String get_name() {
        // TODO Auto-generated method stub
        return "Magnetometer";
    }
    
    @Override
    public String get_service_uuid() {
        // TODO Auto-generated method stub
        return UUID_SERVICE;
    }
    
    @Override
    public String get_data_uuid() {
        // TODO Auto-generated method stub
        return UUID_DATA;
    }
    
    @Override
    public String get_configure_uuid() {
        // TODO Auto-generated method stub
        return UUID_CONFIG;
    }
    
    @Override
    public String get_value_string() {
        float[] data = get_value();
        if (data == null) {
            return "N/A";
        }
        return String.format("x=%+.6f\ny=%+.6f\nz=%+.6f", 
                data[0], data[1], data[2]);
    }
    
    @Override
    protected float[] parse(BluetoothGattCharacteristic c) {
        // Multiply x and y with -1 so that the values correspond with our pretty pictures in the app.
        float x = shortSignedAtOffset(c, 0) * (2000f / 65536f) * -1;
        float y = shortSignedAtOffset(c, 2) * (2000f / 65536f) * -1;
        float z = shortSignedAtOffset(c, 4) * (2000f / 65536f);

        return new float[]{x, y, z};
    }
    
    public void setPeriod(int period) {
        period_ = period;
    }
    
    @Override
    public void update(BluetoothGatt gatt) {
        gatt_char_write(gatt, UUID_PERIOD, new byte[]{(byte) period_});
    }
    
    @Override
    public void enable(BluetoothGatt gatt, boolean enable) {
        super.enable(gatt, enable);
        
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            
        }
        
        setPeriod(50);
        update(gatt);
    }    
}
