package com.ies.blelib.sensor;

import java.util.UUID;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

public abstract class BleSensor<T> {
    private final static String TAG_ = BleSensor.class.getSimpleName();
    
    private static String CHARACTERISTIC_CONFIG = 
            "00002902-0000-1000-8000-00805f9b34fb";
    
    private T data;
    
    public abstract String get_name();
    public abstract String get_service_uuid();
    public abstract String get_data_uuid();
    public abstract String get_configure_uuid();
    protected abstract T parse(BluetoothGattCharacteristic c);
    
    public String get_characteristic_name(String uuid) {
        if (uuid.equalsIgnoreCase(get_data_uuid())) {
            return get_name() + "Data";
        } else if (uuid.equalsIgnoreCase(get_configure_uuid())) {
            return get_name() + "Config";
        }
        
        return "Unknown";
    }
    
    public T get_data() {
        return data;
    }
    
    public void onCharacteristicChanged(BluetoothGattCharacteristic c) {
        data = parse(c);
    }    
    
    private BluetoothGattCharacteristic get_characteristic(
            BluetoothGatt bluetoothGatt, String uuid) {
        final UUID serviceUuid = UUID.fromString(get_service_uuid());
        final UUID characteristicUuid = UUID.fromString(uuid);

        final BluetoothGattService service = 
                bluetoothGatt.getService(serviceUuid);
        return service.getCharacteristic(characteristicUuid);
    }    
    
    public void gatt_char_read(BluetoothGatt gatt, String uuid) {
        BluetoothGattCharacteristic charateristic = 
                get_characteristic(gatt, uuid);
        gatt.readCharacteristic(charateristic);
    }
    
    public void gatt_char_write(
            BluetoothGatt gatt, String uuid, byte[] value) {
        BluetoothGattCharacteristic characteristic = 
                get_characteristic(gatt, uuid);
        characteristic.setValue(value);
        gatt.writeCharacteristic(characteristic);
    }
    
    public void gatt_char_notify(BluetoothGatt gatt, boolean start) {
        UUID CCC = UUID.fromString(CHARACTERISTIC_CONFIG);
        BluetoothGattCharacteristic dataCharacteristic = 
                get_characteristic(gatt, get_data_uuid());
        BluetoothGattDescriptor config = dataCharacteristic.getDescriptor(CCC);
        // enable/disable locally
        gatt.setCharacteristicNotification(dataCharacteristic, start);
        config.setValue(start ? 
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : 
                BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
     // enable/disable remotely
        gatt.writeDescriptor(config);
    }
    
    public byte[] get_config_values(boolean enable) {
        return new byte[] { (byte)(enable ? 1 : 0) };
    }
    
    public void enable(BluetoothGatt gatt, boolean enable) {
        gatt_char_write(gatt, get_configure_uuid(), get_config_values(enable));
    }
}
