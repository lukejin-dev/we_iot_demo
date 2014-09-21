/************************************************************************************
 *
 *  Copyright (C) 2013 HTC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ************************************************************************************/
package com.htc.sample.ble.hrm;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import com.htc.android.bluetooth.le.gatt.BleClientProfile;
import com.htc.android.bluetooth.le.gatt.BleCharacteristic;
import com.htc.android.bluetooth.le.gatt.BleClientService;
import com.htc.android.bluetooth.le.gatt.BleConstants;
import com.htc.android.bluetooth.le.gatt.BleGattID;

public class BluetoothHrmClient extends BleClientProfile {
	private static String TAG = "HeartRateMonitorProfileClient";

	public static final String HRM_CONNECTED = "com.htc.action.hrm_connected";
	public static final String HRM_DISCONNECTED = "com.htc.action.hrm_disconnected";
	public static final String HRM_REGISTERED = "com.htc.action.hrm_registered";

	private static final int HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID = BluetoothHrmService.HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID;
	private static final int HEART_RATE_CONTROL_POINT_CHARACTERISTIC_UUID = BluetoothHrmService.HEART_RATE_CONTROL_POINT_CHARACTERISTIC_UUID;

	private BluetoothHrmService mHeartRateMonitorService = null;
	private Context mContext = null;

	public BluetoothHrmClient(Context context) {
		super(context, new BleGattID(UUID.randomUUID()));
		this.mContext = context;
		Log.d(TAG, "BluetoothHrmClient(Context context)");
		mHeartRateMonitorService = new BluetoothHrmService(context);
		ArrayList<BleClientService> services = new ArrayList<BleClientService>();
		services.add(mHeartRateMonitorService);
		init(services, null);
	}

	public synchronized void deregister() throws InterruptedException {
		deregisterProfile(); 
		wait(5000);
	}

	public void onInitialized(boolean success) {
		Log.d(TAG, "onInitialized");
		if (success) {
			registerProfile();
		}
	}

	public void onDeviceConnected(BluetoothDevice device) {
		Log.d(TAG, "onDeviceConnected");
		
		// if enabling encryption, uncomment line below - or override this method adding line below
		// setEncryption(paramBluetoothDevice, (byte) 3);
		
		mHeartRateMonitorService.registerForNotification(device, 0,
				new BleGattID(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID));
		refresh(device);
		Intent intent = new Intent();
		intent.setAction(HRM_CONNECTED);
		intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
		mContext.sendBroadcast(intent);
	}

	public void onDeviceDisconnected(BluetoothDevice device) {
		Log.d(TAG, "onDeviceDisconnected");
		
		mHeartRateMonitorService.unregisterNotification(device, 0,
				new BleGattID(HEART_RATE_MEASUREMENT_CHARACTERISTIC_UUID));
		Intent intent = new Intent();
		intent.setAction(HRM_DISCONNECTED);
		intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
		mContext.sendBroadcast(intent);
		connectBackground(device);
	}

	public void onRefreshed(BluetoothDevice device) {
		Log.d(TAG, "onRefreshed");
		
		Intent intent = new Intent();
		intent.setAction(HRM_CONNECTED);
		intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
		mContext.sendBroadcast(intent);
	}

	public void onProfileRegistered() {
		Log.d(TAG, "onProfileRegistered");
		Intent intent = new Intent();
		intent.setAction(HRM_REGISTERED);
		mContext.sendBroadcast(intent);
	}

    public void onProfileDeregistered() {
        Log.d(TAG, "onProfileDeregistered");       
    }

    public int writeHeartRateControlPointCharacteristic(BluetoothDevice device, int characteristicID, int value) {
        if (mHeartRateMonitorService == null) {
            return BleConstants.SERVICE_UNAVAILABLE;
        }

		BleCharacteristic characteristic = mHeartRateMonitorService
				.getCharacteristic(device, new BleGattID(
						HEART_RATE_CONTROL_POINT_CHARACTERISTIC_UUID));
		
		if(characteristic == null) {
			characteristic = new BleCharacteristic(new BleGattID(characteristicID));
		}
		characteristic.setValue(value);

        return mHeartRateMonitorService.writeCharacteristic(device,0,characteristic);
    }

    public int readBodySensorLocationCharacteristic(BluetoothDevice device) {
        if (mHeartRateMonitorService == null) {
            return BleConstants.SERVICE_UNAVAILABLE;
        }

        BleCharacteristic characteristic = new BleCharacteristic(new BleGattID(BluetoothHrmService.HEART_RATE_BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID));
        return mHeartRateMonitorService.readCharacteristic(device, characteristic); 
    }
}
