/**************************************************************************************************
  Filename:       LeController.java
  Revised:        $Date: 2013-08-30 11:44:31 +0200 (fr, 30 aug 2013) $
  Revision:       $Revision: 27454 $

  Copyright 2013 Texas Instruments Incorporated. All rights reserved.
 
  IMPORTANT: Your use of this Software is limited to those specific rights
  granted under the terms of a software license agreement between the user
  who downloaded the software, his/her employer (which must be your employer)
  and Texas Instruments Incorporated (the "License").  You may not use this
  Software unless you agree to abide by the terms of the License. 
  The License limits your use, and you acknowledge, that the Software may not be 
  modified, copied or distributed unless used solely and exclusively in conjunction 
  with a Texas Instruments Bluetooth® device. Other than for the foregoing purpose, 
  you may not use, reproduce, copy, prepare derivative works of, modify, distribute, 
  perform, display or sell this Software and/or its documentation for any purpose.
 
  YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
  PROVIDED “AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
  INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
  NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL
  TEXAS INSTRUMENTS OR ITS LICENSORS BE LIABLE OR OBLIGATED UNDER CONTRACT,
  NEGLIGENCE, STRICT LIABILITY, CONTRIBUTION, BREACH OF WARRANTY, OR OTHER
  LEGAL EQUITABLE THEORY ANY DIRECT OR INDIRECT DAMAGES OR EXPENSES
  INCLUDING BUT NOT LIMITED TO ANY INCIDENTAL, SPECIAL, INDIRECT, PUNITIVE
  OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF PROCUREMENT
  OF SUBSTITUTE GOODS, TECHNOLOGY, SERVICES, OR ANY CLAIMS BY THIRD PARTIES
  (INCLUDING BUT NOT LIMITED TO ANY DEFENSE THEREOF), OR OTHER SIMILAR COSTS.
 
  Should you have any questions regarding your right to use this Software,
  contact Texas Instruments Incorporated at www.TI.com

 **************************************************************************************************/
package com.ti.sensortag.ble;

import static android.bluetooth.BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
import static com.ti.sensortag.ble.Sensor.SIMPLE_KEYS;
import static com.ti.sensortag.models.Devices.State.ADVERTISING;
import static com.ti.sensortag.models.Devices.State.CONNECTED;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ti.sensortag.models.Devices;

//TODO: change to allow multiple devices to connect.
public enum LeController implements PropertyChangeListener {
  INSTANCE;

  public final static String TAG = "LeController";
  final WriteQueue writeQueue = new WriteQueue();

  // See ScanType enum's definition for details.
  public static ScanType scanType = getScanType();

  private final AdListener adListener = new AdListener();

  private final BluetoothGattCallback callback = new Callback(this);
  private LeScanCallback leScanCallback;

  private BluetoothGatt bluetoothGatt;
  private BluetoothAdapter mBluetoothAdapter = null;
  private boolean serviceDiscoveryRunning = false;

  public static final UUID CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

  private Activity activity;

  private BluetoothManager bluetoothManager;

  private final ScanRestarter scanRestarter = new ScanRestarter();

  private BluetoothGattService getService(Sensor sensor) {
    return bluetoothGatt.getService(sensor.getService());
  }

  void onServicesDiscovered(final BluetoothDevice device, int status) {
    serviceDiscoveryRunning = false;

    if (status != BluetoothGatt.GATT_SUCCESS)
      return;

    for (Sensor sensor : Sensor.values()) {
      BluetoothGattService gattService = getService(sensor);
      logEvent(gattService != null, "Unable to get service for " + sensor);

      if (isEnabledByPrefs(sensor)) {
        changeNotificationStatus(gattService, sensor, true);
        changeSensorStatus(gattService, sensor, true);
      }
    }

    if (isEnabledByPrefs(Sensor.BAROMETER)) {
      calibrateBarometer();
    }
  }

  /**
   * Calibrating the barometer includes
   * 
   * 1. Write calibration code to configuration characteristic. 2. Read calibration values from sensor, either with notifications or a normal read. 3. Use
   * calibration values in formulas when interpreting sensor values.
   */
  public void calibrateBarometer() {
    writeQueue.queueRunnable(new Runnable() {
      public void run() {
        UUID configUuid = Sensor.BAROMETER.getConfig();
        BluetoothGattCharacteristic config = getService(Sensor.BAROMETER).getCharacteristic(configUuid);

        byte[] callibrationCode = new byte[] { 2 };

        boolean successLocalySetValue = config.setValue(callibrationCode);
        logEvent(successLocalySetValue, "Unable to locally set the enable code.");

        boolean success = bluetoothGatt.writeCharacteristic(config);
        logEvent(success, "Unable to initiate the write that configures " + Sensor.BAROMETER);
      }
    });
    writeQueue.queueRunnable(new Runnable() {
      public void run() {
        BluetoothGattCharacteristic calibrationCharacteristic = getService(Sensor.BAROMETER).getCharacteristic(SensorTag.UUID_BAR_CALI);
        boolean success = bluetoothGatt.readCharacteristic(calibrationCharacteristic);
        logEvent(success, "Unable to read calibration values.");
      }
    });
    writeQueue.queueRunnable(new Runnable() {
      public void run() {
        UUID configUuid = Sensor.BAROMETER.getConfig();
        BluetoothGattCharacteristic config = getService(Sensor.BAROMETER).getCharacteristic(configUuid);

        byte[] enableCode = new byte[] { 1 };

        boolean successLocalySetValue = config.setValue(enableCode);
        logEvent(successLocalySetValue, "Unable to locally set the enable code.");

        boolean success = bluetoothGatt.writeCharacteristic(config);
        logEvent(success, "Unable to initiate the write that configures " + Sensor.BAROMETER);
      }
    });
  }

  private static ScanType getScanType() {
    // Some models provide only one scanResult per scan (Nexus 4, Nexus 7 2013)
    ScanType scanType = ScanType.CONTINUOUS;

    if (Build.MODEL.equals("Nexus 4"))
      scanType = ScanType.ONE_OFF;

    // Nexus 7 (2013)
    if (Build.MODEL.equals("Nexus 7") && Build.DISPLAY.equals("JSS15J"))
      scanType = ScanType.ONE_OFF;

    return scanType;
  }

  private void changeNotificationStatus(final BluetoothGattService gattService, final Sensor sensor, final boolean enable) {
    writeQueue.queueRunnable(new Runnable() {
      @Override
      public void run() {
        BluetoothGattCharacteristic dataCharacteristic = gattService.getCharacteristic(sensor.getData());
        logEvent(bluetoothGatt.setCharacteristicNotification(dataCharacteristic, true), "The notification status was changed.",
            "Failed to set the notification status.");

        BluetoothGattDescriptor config = dataCharacteristic.getDescriptor(CCC);
        logEvent(config != null, "Unable to get config descriptor.");

        byte[] configValue = enable ? ENABLE_NOTIFICATION_VALUE : DISABLE_NOTIFICATION_VALUE;
        boolean success = config.setValue(configValue);
        logEvent(success, "Could not locally store value.");

        logEvent(bluetoothGatt.writeDescriptor(config), "Initiated a write to descriptor.", "Unable to initiate write.");
      }
    });
  }

  boolean isEnabledByPrefs(final Sensor sensor) {
    String preferenceKeyString = "pref_" + sensor.name().toLowerCase(Locale.ENGLISH) + "_on";

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

    Boolean defaultValue = true;
    // defaultValue should never be used since in MainActivity.onCreate
    // we do PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    boolean isEnabled = prefs.getBoolean(preferenceKeyString, defaultValue);
    return isEnabled;
  }

  private void discoverServices(BluetoothDevice device) {
    if (serviceDiscoveryRunning)
      return;

    // discoverServices triggers the callback onServicesDiscovered
    // whether it succeeds or not.
    serviceDiscoveryRunning = bluetoothGatt.discoverServices();

    if (serviceDiscoveryRunning) {
      activity.runOnUiThread(new Runnable() {
        public void run() {
          Toast.makeText(activity, "Discovering services...", Toast.LENGTH_SHORT).show();
        }
      });

      Log.i(TAG, "Started service discovery.");
    } else {
      activity.runOnUiThread(new Runnable() {
        public void run() {
          Toast.makeText(activity, "Unable to start service discovery.", Toast.LENGTH_SHORT).show();
        }
      });
    }
  }

  void logEvent(boolean success, String failureMessage) {
    if (!success) {
      Log.e(TAG, failureMessage);
    }
  }

  void logEvent(boolean success, String succesMsg, String failureMessage) {
    if (success)
      Log.i("Custom", succesMsg);
    else
      Log.e("Custom", failureMessage);
  }

  public void onDestroy() {
    Devices.INSTANCE.removePropertyChangeListener(this);

    // Probably not necessary since onPause stops the scan.
    if (mBluetoothAdapter != null) {
      mBluetoothAdapter.stopLeScan(leScanCallback);
      mBluetoothAdapter = null;
    }

    shutdownConnection();
  }

  /**
   * Synchronized because otherwise the threads might see outdated copies of this singletons fields.
   */
  @Override
  public synchronized void propertyChange(PropertyChangeEvent event) {
    if (event.getPropertyName().equals(Devices.NEW_DEVICE_ + CONNECTED.name())) {
      BluetoothDevice device = (BluetoothDevice) event.getNewValue();
      Log.i(TAG, "Connected");

      List<BluetoothGattService> services = bluetoothGatt.getServices();
      int numServices = services.size();
      if (numServices > 0) {
        Log.i(TAG, "Skipping service discovery since we already have " + numServices + " services cached.");
        onServicesDiscovered(device, BluetoothGatt.GATT_SUCCESS);
      } else {
        discoverServices(device);
      }
    }
  }

  public void run(Context context) {
    this.activity = (Activity) context;

    Devices.INSTANCE.addPropertyChangeListener(this);

    bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

    mBluetoothAdapter = bluetoothManager.getAdapter();
    if (mBluetoothAdapter == null) {
      Log.wtf(TAG, "Adapter is null.");
    }
  }

  /**
   * @return true if connection attempt was initiated successfully.
   * */
  synchronized public void connect(BluetoothDevice device) {
    if (bluetoothGatt != null) {
      Log.w(TAG, "Attempted to connect while having a connection already up. Should close the old connection before starting a new one.");
      shutdownConnection();
      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    bluetoothGatt = device.connectGatt(activity, false, callback);
    if (bluetoothGatt == null) {
      Log.wtf(TAG, "connectGatt failed.");
    }
  }

  /**
   * Complete shutdown of connection. Disconnect, close, and flush the write queue for any unprocessed ble operations.
   */
  public void shutdownConnection() {
    flushWriteQueue();
    serviceDiscoveryRunning = false;
    if (bluetoothGatt == null) {
      return;
    }

    bluetoothGatt.disconnect();
    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    bluetoothGatt.close();
    bluetoothGatt = null;
  };

  public boolean startScan() {
    if (mBluetoothAdapter == null) {
      // Could be that this public singleton is asked to scan before
      // the adapter is initialized in run.
      Log.w(TAG, "Could not start scanning, mBluetoothAdapter not initialized.");
      return false;
    }

    leScanCallback = new LeScanCallback() {
      public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        // Only react to SensorTags
        boolean isSensorTag = device.getName().equals("SensorTag");
        if (!isSensorTag)
          return;

        activity.runOnUiThread(new Runnable() {
          public void run() {
            Devices.INSTANCE.setState(ADVERTISING, device);
          }
        });

        adListener.onScanResult(device);
      }
    };

    scanRestarter.startScan(leScanCallback, mBluetoothAdapter);
    adListener.startListeningToScanResults();

    return true;
  }

  public void stopScan() {
    scanRestarter.stopLeScan(leScanCallback, mBluetoothAdapter);
    adListener.stopListeningToScanResults();
    Log.i("Custom", "Scanning stopped.");
  }

  private void changeSensorStatus(final BluetoothGattService service, final Sensor sensor, final boolean enabled) {
    if (sensor == SIMPLE_KEYS) // Simple keys are always enabled.
      return;

    writeQueue.queueRunnable(new Runnable() {
      @Override
      public void run() {
        BluetoothGattCharacteristic config = service.getCharacteristic(sensor.getConfig());

        byte[] code = enabled ? sensor.getEnableSensorCode() : new byte[] { 0 };

        boolean successLocalySetValue = config.setValue(code);
        logEvent(successLocalySetValue, "Unable to locally set the enable code.");

        boolean success = bluetoothGatt.writeCharacteristic(config);
        logEvent(success, "Unable to initiate the write that turns on/off " + sensor);
      }
    });
  }

  public void enableSensor(Sensor sensor) {
    changeSensorStatus(getService(sensor), sensor, true);
    changeNotificationStatus(getService(sensor), sensor, true);
  }

  public void disableSensor(Sensor sensor) {
    changeSensorStatus(getService(sensor), sensor, false);
    changeNotificationStatus(getService(sensor), sensor, false);
  }

  public List<BluetoothDevice> getConnectedDevices() {
    return bluetoothManager == null ? new ArrayList<BluetoothDevice>() : bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
  }

  public void flushWriteQueue() {
    writeQueue.flush();
  }
}
