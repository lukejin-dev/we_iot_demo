package com.htc.sample.ble.hrm;

import java.util.ArrayList;
import java.util.Iterator;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.htc.android.bluetooth.le.gatt.BleClientService;
import com.htc.android.bluetooth.le.gatt.BleCharacteristic;
import com.htc.android.bluetooth.le.gatt.BleConstants;
import com.htc.android.bluetooth.le.gatt.BleDescriptor;
import com.htc.android.bluetooth.le.gatt.BleGattID;

import java.util.Timer;
import java.util.TimerTask;

public class BluetoothHrmService extends BleClientService {
    public static String TAG = "HeartRateMonitorServiceClient";
    
    public static final String PROFILE_UUID = "0000180D-0000-1000-8000-00805f9b34fb";
    
    public static final String HRM_MEASUREMENT = "com.htc.action.hrm_measurement";
    public static final String EXTRA_HRMVALUE = "extra_hrmvalue";
    public static final String EXTRA_HRMSENSORCONTACTSTATUS = "extra_sensor_contact_status";    
    public static final String EXTRA_ENERGY_EXPENDED = "extra_energy_expended";
    public static final String EXTRA_RR_INTERVAL = "extra_rr_interval";

    public static final String HRM_BODY_SENSOR_LOCATION = "com.htc.action.hrm_body_sensor_location";
    public static final String EXTRA_LOCATION = "extra_location";
    
    public static final int HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID = 0x2a37;
    public static final int HEART_RATE_BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID = 0x2a38;
    public static final int HEART_RATE_CONTROL_POINT_CHARACTERISTIC_UUID = 0x2a39;
        
    public static  BleGattID myUuid = new BleGattID(PROFILE_UUID); 
    
    private Context mContext = null;
    
    public BluetoothHrmService(Context context) {
        super(myUuid);
        Log.d(TAG, "BluetoothHrmService");
        mContext = context;
    }

    public void onWriteCharacteristicComplete(int status, BluetoothDevice d,  BleCharacteristic characteristic) {
        Log.d(TAG, "onWriteCharacteristicComplete");
    }

    public void characteristicsRetrieved(BluetoothDevice d) {
        Log.d(TAG, "characteristicsRetrieved");
    }

    public void onRefreshComplete(BluetoothDevice d) {
        Log.d(TAG, "onRefreshComplete");
                
        ArrayList<BleCharacteristic> characteristics = getAllCharacteristics(d);
        Log.d(TAG, "Characteristics: " + characteristics.size());

        /* Writes the Client Characteristic Configuration for the Heart Rate measurement Characteristic */
        Iterator<BleCharacteristic> it = characteristics.iterator();
        while(it.hasNext()) {
             BleCharacteristic tempCharacteristic = it.next();
                
            byte[] value = { 0x01, 0x00 };
            BleDescriptor clientConfig = tempCharacteristic.getDescriptor(new BleGattID(BleConstants.GATT_UUID_CHAR_CLIENT_CONFIG16));
            if(clientConfig != null) {
                clientConfig.setValue(value);
                clientConfig.setWriteType(BleConstants.GATTC_TYPE_WRITE_NO_RSP);
                writeCharacteristic(d, 0, tempCharacteristic);
            }
        }

        //Queing is not handling in the lower layers, so sending request after some delay
        final BluetoothDevice device = d;
        Timer timer = new Timer();        
        timer.schedule( new TimerTask(){
           public void run() {
                /* Try to read the body sensor location characteristic if present */
                BleCharacteristic characteristic = new BleCharacteristic(new BleGattID(HEART_RATE_BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID));
                readCharacteristic(device, characteristic);
           }
         }, 2000);

        
    }

    public void onSetCharacteristicAuthRequirement(BluetoothDevice d, BleCharacteristic characteristic, int instanceID) {
        Log.d(TAG, "onSetCharacteristicAuthRequirement");
    }

    public void onReadCharacteristicComplete(BluetoothDevice d, BleCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        Log.d(TAG, "onReadCharacteristicComplete - char = " + characteristic.getID().toString() +     " + value len = " + data.length + ", 1st byte = " + data[0]);        
      
        Intent intent = new Intent();
        intent.setAction(HRM_BODY_SENSOR_LOCATION);
        intent.putExtra(EXTRA_LOCATION, unsignedByteToInt(data[0]));
        mContext.sendBroadcast(intent);    
            
    }

    public void onCharacteristicChanged(BluetoothDevice d, BleCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        byte flags;
        boolean bvalid = true;
        boolean rrIntervalFlag = false;
        int hrmValue = 0, rrIntervalValue = 0, energyExpendedValue = 0, sensorContactStatus;
        Log.d(TAG, "onCharacteristicChanged - char = " + characteristic.getID().toString() +     " + value len = " + data.length + ", 1st byte = " + data[0]);
        for(int i=0;i<data.length;i++) {
            Log.d(TAG, "data[" + i +  "] = " + data[i]);
        }
        if (data.length > 1) {
            flags = data[0];
            /* This is present in the 2 & 3 Bits */
            sensorContactStatus =  flags & 0x06;
            sensorContactStatus = sensorContactStatus >> 1;
            Log.d(TAG, "sensor contact status " + sensorContactStatus);
        }
        else {
            Log.w(TAG, " onCharacteristicChanged - Receive no data");
            return;
        }
        
        /* Check whether the heart rate value is one or two octets in the flag and assign respective values  */
        int length = 1; 
        if((flags & 0x01) == 0x01 ) {
            if (data.length > 2) {
                hrmValue = unsigned2BytestoInt(data[1], data[2]);
                length +=2;
                /* Check whether  energy expended field is present or not */
                if((flags & 0x08) == 0x08 ) {
                    if ( data.length > 4 ) {
                        energyExpendedValue = unsigned2BytestoInt(data[3], data[4]);
                        length +=2;
                        /* Check whether RR Interval is present or not  */
                        if((flags & 0x10) == 0x10 ) {
                            if ( data.length > 6 ) {
                                rrIntervalValue = unsigned2BytestoInt(data[5], data[6]);    
                                rrIntervalFlag = true;
                                length +=2;
                            } else {
                                bvalid = false;
                            }
                        }
                    } else {
                        bvalid = false;
                    }
                } else {
                    /* expended energy is not supported */
                    energyExpendedValue = -1;
                    /* Check whether RR Interval is present or not  */
                    if((flags & 0x10) == 0x10 ) {
                        if ( data.length > 4 ) {
                            rrIntervalValue = unsigned2BytestoInt(data[3], data[4]);
                            rrIntervalFlag = true;
                            length +=2;
                        } else {
                            bvalid = false;
                        }
                    }                                    
                }
            } else {
                bvalid = false;
            }
        } else {
            if ( data.length > 1) {
                hrmValue = unsignedByteToInt(data[1]);
                length +=1;
            } else {
                bvalid = false;
            }

            /* Check whether  energy expended field is present or not */
            if((flags & 0x08) == 0x08 ) {
                if (data.length > 3) {
                    energyExpendedValue = unsigned2BytestoInt(data[2], data[3]);
                    length +=2;
                    
                    /* Check whether RR Interval is present or not  */
                    if((flags & 0x10) == 0x10) {
                        if ( data.length > 5 ) {
                            rrIntervalValue = unsigned2BytestoInt(data[4], data[5]);
                            rrIntervalFlag = true;
                            length += 2;
                        } else {
                            bvalid = false;
                        }
                    } 
                } else {
                    bvalid = false;
                }
            } else {
                /* expended energy is not supported */
                energyExpendedValue = -1;
                /* Check whether RR Interval is present or not  */
                if((flags & 0x10) == 0x10) {
                    if ( data.length > 3 ) {
                        rrIntervalValue = unsigned2BytestoInt(data[2], data[3]);
                        rrIntervalFlag = true;
                        length += 2;
                    } else {
                        bvalid = false;
                    }
                }                                    
            }
        }

        if ( bvalid == false ) {
            Log.e(TAG, " received invalid data from sensor");
            return;
        }


        Intent intent = new Intent();
        intent.setAction(HRM_MEASUREMENT);
        intent.putExtra(EXTRA_HRMVALUE, hrmValue);
        intent.putExtra(EXTRA_ENERGY_EXPENDED, energyExpendedValue);                           
        intent.putExtra(EXTRA_HRMSENSORCONTACTSTATUS, sensorContactStatus);                
        
        if(rrIntervalFlag) {
            int rrInveterelSize = 1;
            if(length < data.length) {
                rrInveterelSize = (data.length - length)/2 + rrInveterelSize;
             }
            
            int rrIntervalArray[] = new int[rrInveterelSize];
            rrIntervalArray[0] = rrIntervalValue;
            int j = length;
            
            for(int i=1;((i<rrIntervalArray.length) && (j+1 <= data.length)); i++) {            
                rrIntervalArray[i] = unsigned2BytestoInt(data[j], data[j+1]);
                j = j+2;
            }   

             intent.putExtra(EXTRA_RR_INTERVAL, rrIntervalArray);    
        }
        else {
            int rrIntervalArray[] = new int[1];
            rrIntervalArray[0] = -1; // invalid RR interval value
            intent.putExtra(EXTRA_RR_INTERVAL, rrIntervalArray);    
        }    
        
        mContext.sendBroadcast(intent);
    }
    
    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    private static int unsigned2BytestoInt(byte firstOctet, byte secondOctet) {
        int value = 0;
        value = (secondOctet & 0x00FF);
        value = value << 8;
        value = value |(firstOctet & 0x00FF);
        return value;
    }
}
