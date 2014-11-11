package com.ies.blelib.sensor;

import com.google.gson.Gson;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;

public class TiAccelerometerSensor extends TiSensor<float[]> {

    public static final String UUID_SERVICE = 
            "f000aa10-0451-4000-b000-000000000000";
    private static final String UUID_DATA = 
            "f000aa11-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = 
            "f000aa12-0451-4000-b000-000000000000";
    private static final String UUID_PERIOD = 
            "f000aa13-0451-4000-b000-000000000000";
    
    private static final int PERIOD_MIN = 10;
    private static final int PERIOD_MAX = 255;

    private int period_ = PERIOD_MIN;
    
    @Override
    public String get_name() {
        // TODO Auto-generated method stub
        return "Accelerator";
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
    public void update(BluetoothGatt gatt) {
        gatt_char_write(gatt, UUID_PERIOD, new byte[]{(byte) period_});
    }
    
    @Override
    public float[] parse(final BluetoothGattCharacteristic c) {
    /*
     * The accelerometer has the range [-2g, 2g] with unit (1/64)g.
     *
     * To convert from unit (1/64)g to unit g we divide by 64.
     *
     * (g = 9.81 m/s^2)
     *
     * The z value is multiplied with -1 to coincide
     * with how we have arbitrarily defined the positive y direction.
     * (illustrated by the apps accelerometer image)
     * */

        Integer x = c.getIntValue(FORMAT_SINT8, 0);
        Integer y = c.getIntValue(FORMAT_SINT8, 1);
        Integer z = -1 * c.getIntValue(FORMAT_SINT8, 2);

        double scaledX = x / 64.0;
        double scaledY = y / 64.0;
        double scaledZ = z / 64.0;

        return new float[]{(float)scaledX, (float)scaledY, (float)scaledZ};
    }

    @Override
    public boolean onCharacteristicRead(BluetoothGattCharacteristic c) {
        super.onCharacteristicRead(c);

        if ( !c.getUuid().toString().equals(UUID_PERIOD) )
            return false;

        period_ = shortUnsignedAtOffset(c, 0);
        return true;
    }
    
    public int getMinPeriod() {
        return PERIOD_MIN;
    }

    public int getMaxPeriod() {
        return PERIOD_MAX;
    }

    public void setPeriod(int period) {
        period_ = period;
    }

    public int getPeriod() {
        return period_;
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
