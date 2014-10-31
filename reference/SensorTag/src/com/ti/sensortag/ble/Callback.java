/**************************************************************************************************
  Filename:       Callback.java
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

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_SINT8;
import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static com.ti.sensortag.models.Devices.State.ADVERTISING;
import static com.ti.sensortag.models.Devices.State.CONNECTED;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import com.ti.sensortag.models.Devices;
import com.ti.sensortag.models.Devices.State;

/**
 * A couple of problems that don't come up in the documentation:
 * 
 * If an exception is thrown from one of your callback methods, it will be caught with catch-all exception handling in BluetoothGatt and logged with priority
 * warning.
 * 
 * This is unfortunate because you do not get a stack trace, and because log messages at priority warning are easily overlooked. To compensate for this we do
 * our own exception handling and print the stack traces.
 */
class Callback extends BluetoothGattCallback {

  protected static final String TAG = "Callback";
  private final LeController leController;

  Callback(LeController leStateMachine) {
    this.leController = leStateMachine;
  }

  @Override
  public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
    try {
      new Thread(new Runnable() {
        @Override
        public void run() {
          UUID uuid = characteristic.getUuid();
          final Sensor sensor = Sensor.getFromDataUuid(uuid);
          sensor.onCharacteristicChanged(characteristic);
        }
      }).start();
    } catch (Exception e) {
      Log.e(TAG, "", e);
    }
  }

  @Override
  public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    try {
      this.leController.logEvent(status == BluetoothGatt.GATT_SUCCESS, "Successfully read characteristic.", "Failed at reading characteristic");

      // Barometer calibration values are read.
      List<Integer> cal = new ArrayList<Integer>();
      for (int offset = 0; offset < 8; offset += 2) {
        Integer lowerByte = characteristic.getIntValue(FORMAT_UINT8, offset);
        Integer upperByte = characteristic.getIntValue(FORMAT_UINT8, offset + 1); // Note: interpret MSB as signed.
        cal.add((upperByte << 8) + lowerByte);
      }

      for (int offset = 8; offset < 16; offset += 2) {
        Integer lowerByte = characteristic.getIntValue(FORMAT_UINT8, offset);
        Integer upperByte = characteristic.getIntValue(FORMAT_SINT8, offset + 1); // Note: interpret MSB as signed.
        cal.add((upperByte << 8) + lowerByte);
      }

      BarometerCalibrationCoefficients.INSTANCE.barometerCalibrationCoefficients = cal;

      this.leController.writeQueue.issue();
    } catch (Exception e) {
      Log.e(TAG, "", e);
    }
  }

  @Override
  public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
    try {
      String msg = String.format("Callback.onConnectionStateChange(%s, %s, %s);", gatt.getDevice().getAddress(), BluetoothGattUtils.decodeReturnCode(status),
          (newState == STATE_CONNECTED ? CONNECTED : State.SILENT));

      Log.i(TAG, msg);
      if (status == GATT_SUCCESS) {
        State newStateEnum = newState == STATE_CONNECTED ? CONNECTED : ADVERTISING;
        Devices.INSTANCE.setState(newStateEnum, gatt.getDevice());
      }
      this.leController.flushWriteQueue();

    } catch (Exception e) {
      Log.e(TAG, "", e);
    }
  }

  @Override
  public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
    try {
      this.leController.logEvent(status == GATT_SUCCESS, "Descriptor write gave status return code: " + BluetoothGattUtils.decodeReturnCode(status));

      this.leController.writeQueue.issue();
    } catch (Exception e) {
      Log.e(TAG, "", e);
    }
  }

  @Override
  public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
    try {
      this.leController.logEvent(status == GATT_SUCCESS, "Characteristic write gave status return code: " + BluetoothGattUtils.decodeReturnCode(status));

      this.leController.writeQueue.issue();
    } catch (Exception e) {
      Log.e(TAG, "", e);
    }
  }

  /**
   * NB: On the first service discovery between a server-client pair the discovery might take several seconds. It has been observed to take 8 seconds. But on
   * subsequent discovery calls, the client will have the services cached and it will take milliseconds.
   * */
  @Override
  public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
    try {
      final BluetoothDevice device = gatt.getDevice();
      String successMsg = "Service discovery successfully completed for " + device.getName();
      String failureMsg = "Service discovery failed for " + device.getName();
      leController.logEvent(status == GATT_SUCCESS, successMsg, failureMsg);

      new Thread(new Runnable() {
        public void run() {
          leController.onServicesDiscovered(device, status);
        }
      }).start();
    } catch (Exception e) {
      Log.e(TAG, "", e);
    }
  }
}