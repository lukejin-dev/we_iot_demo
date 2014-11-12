/**
 * CopyRight (c) 2014, Infoengine technology (Shenzhen) Ltd Reserved.
 * 
 * @author ken
 */

package ies.iot.demolib.utils;

import java.util.ArrayList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class BleUtil {
    private static final String TAG = "BleUtil";
    
    /**
     * Get android bluetooth adapter.
     * 
     * @param context Activity context
     * @return android bluetooth adapter.
     */
    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        final BluetoothManager bluetoothManager =
         (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null)
            return null;
        return bluetoothManager.getAdapter();
    }
    
    /**
     * Check whether device support ble.
     * 
     * @param context Activity context
     * @return whether device support ble
     */
    public static boolean CheckBleSupport(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE);
    }
    
    /**
     * Enable bluetooth.
     * 
     * @param context Activity context
     * @return android bluetooth adapter
     */
    public static BluetoothAdapter EnableBle(Context context) {
        BluetoothAdapter adapter = getBluetoothAdapter(context);
        if (!adapter.enable()) {
            return null;
        }
        return adapter;
    }
    
    public static byte[] MACStringToByteArray(String address) {
        String[] addrs = address.split(":");
        byte[] mac_bytes = new byte[6];
        for (int i = 0; i < addrs.length; i ++) {
            mac_bytes[i] = Integer.decode("0x" + addrs[i]).byteValue();
        }
        return mac_bytes;
    }
    
    public static String ByteArrayToString(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (byte item:arr) {
            sb.append(String.format("%02X ", item & 0xFF));
        }
        
        return "{ " + sb.toString() + "}";
    }
    
    public static byte[] StringToByteArray(String value) {
        ArrayList<Byte> ret_list = new ArrayList<Byte>();
        String temp = value.substring(1, value.length()-1);
        String[] str_arr = temp.split(" ");
        for (String str_item:str_arr) {
            if (str_item.length() == 0) {
                continue;
            }
            
            int v = Integer.parseInt(str_item, 16);
            ret_list.add((byte) (v & 0xFF));
        }
        byte[] ret_val = new byte[ret_list.size()];
        for (int index = 0; index < ret_list.size(); index ++) {
            ret_val[index] = ret_list.get(index);
        }
        return ret_val;
    }

    
    public static boolean ByteArrayCompare(byte[] a1, byte[] a2) {
        if (a1 == null || a2 == null || a1.length == 0 || a2.length == 0) {
            return false;
        }
        if (a1.length != a2.length) {
            return false;
        }
        for (int index = 0; index < a1.length; index ++) {
            if (a1[index] != a2[index]) {
                return false;
            }
        }
        
        return true;
    }
    
    public static byte[] SubArray(byte[] origin, int offset, int length) {
        if (origin == null || origin.length == 0) {
            return null;
        }
        
        int actual_length = length;
        if (offset + length > origin.length) {
            Log.v(TAG, "Short length from " + length + " to " + actual_length);
            actual_length = origin.length - offset;
        }
        
        byte[] newarr = new byte[length];
        for (int index = offset; index < offset + actual_length; index ++) {
            newarr[index - offset] = origin[index];
        }
        
        return newarr;
    }
}
