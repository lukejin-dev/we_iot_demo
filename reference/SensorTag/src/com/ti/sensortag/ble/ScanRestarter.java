/**************************************************************************************************
  Filename:       ScanRestarter.java
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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.util.Log;

/**
 * This class periodically restarts scanning if the device is of the scan type one-off. See ScanType for more information about how different devices scan
 * differently.
 */
public class ScanRestarter {

  protected static final String TAG = "ScanRestarter";
  @SuppressWarnings("rawtypes")
  private ScheduledFuture handle;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private Boolean firstRun = null;
  private int period = AdListener.TIME_OUT_PERIOD / 2;

  public void startScan(final LeScanCallback callback, final BluetoothAdapter bluetoothAdapter) {
    if (LeController.scanType == ScanType.CONTINUOUS) {
      bluetoothAdapter.startLeScan(callback);
      return;
    }

    firstRun = true;
    final Runnable restartScanRunnable = new Runnable() {
      public void run() {
        try {
          if (firstRun) {
            firstRun = false;
            bluetoothAdapter.startLeScan(callback); // TODO: check return value
          } else {
            bluetoothAdapter.stopLeScan(callback);
            bluetoothAdapter.startLeScan(callback);
          }
        } catch (Exception e) {
          // NB: We do infamous Catch-em-all exception handling because if we
          // don't catch the
          // exception it will be silently ignored by the scheduler.
          Log.e(TAG, "", e);
        }
      };
    };

    // restartScanRunnable will be run on the thread that calls
    // scheduleAtFixedRate,
    // which is why we want to create a new thread just for that call.
    new Thread(new Runnable() {
      public void run() {
        handle = scheduler.scheduleAtFixedRate(restartScanRunnable, period, period, TimeUnit.MILLISECONDS);
      }
    }).start();
  }

  public void stopLeScan(LeScanCallback leScanCallback, BluetoothAdapter bluetoothAdapter) {
    if (LeController.scanType == ScanType.CONTINUOUS) {
      bluetoothAdapter.stopLeScan(leScanCallback); // Must be same instance that
                                                   // started the scan
      return;
    }

    boolean mayInterruptIfRunning = true;
    handle.cancel(mayInterruptIfRunning);
    bluetoothAdapter.stopLeScan(leScanCallback);
  }
}
