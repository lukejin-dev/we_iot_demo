/**************************************************************************************************
  Filename:       AdListener.java
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

import static com.ti.sensortag.models.Devices.State.ADVERTISING;
import static com.ti.sensortag.models.Devices.State.CONNECTED;
import static com.ti.sensortag.models.Devices.State.SILENT;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.ti.sensortag.models.Devices;

/**
 * This class is responsible for moving devices from the advertising list to the silent list if they have not advertised in the past x milliseconds.
 * 
 * It does this by listening to the scan results and comparing the observed advertisements to the advertisement list.
 * 
 * Also it does a sanity check on the connected devices.
 * 
 * TODO: Improve code scaleability by making a Scanner model, the Scanner model would fire a property change event when starting and stopping scanning. It would
 * also notify listeners of scan results with a property change event. This would sever the dependency from Callback to AdListener.
 * */
public class AdListener {
  public final static String TAG = "AdListener";

  public static final int TIME_OUT_PERIOD = 600;

  private Set<BluetoothDevice> advertisersAtStart = null;
  private Set<BluetoothDevice> advertisersDuringPeriod = null;

  @SuppressWarnings("rawtypes")
  private ScheduledFuture handle;
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  private final Runnable runnable = new Runnable() {
    public void run() {
      try {
        refreshAdvertisingDevices();
        refreshConnectedDevices();
      } catch (Exception e) {
        Log.e(TAG, "", e);
      }
    }

    /**
     * This method is completely independent of refreshAdvertisingDevices, it just happens to be convenient for it to run periodically like
     * refreshAdvertisingDevices does.
     * */
    private void refreshConnectedDevices() {
      List<BluetoothDevice> connectedActual = LeController.INSTANCE.getConnectedDevices();
      Set<BluetoothDevice> connectedModel = Devices.State.CONNECTED.getDevices();
      connectedModel.removeAll(connectedActual);
      for (BluetoothDevice notConnected : connectedModel) {
        Log.w("Custom", "Had to use sanity check, should not be necessary with proper connection handling.");
        Devices.INSTANCE.setState(SILENT, notConnected);
      }
    }

    private void refreshAdvertisingDevices() {
      // A bit too spammy to have this logging.
      // But it is quite usefull when the device states are acting weird,
      // so i'll keep it commented out, not deleted, for now.

      // Log.v(TAG, String.format("AdListener.refreshAdvertisingDevices(){" +
      // "\n advertisersAtStart = %s;" +
      // "\n advertisersDuringPeriod = %s;" +
      // "\n SILENT.getDevices() = %s;" +
      // "\n ADVERTISING.getDevices() = %s;" +
      // "\n CONNECTED.getDevices() = %s;\n}",
      //
      // advertisersAtStart,
      // advertisersDuringPeriod,
      // SILENT.getDevices(),
      // ADVERTISING.getDevices(),
      // CONNECTED.getDevices()));

      if (advertisersAtStart != null) {
        advertisersAtStart.removeAll(advertisersDuringPeriod);
        advertisersAtStart.removeAll(CONNECTED.getDevices());
        for (BluetoothDevice silentDevice : advertisersAtStart) {
          Devices.INSTANCE.setState(SILENT, silentDevice);
        }
      }
      advertisersAtStart = ADVERTISING.getDevices();
      advertisersDuringPeriod = Collections.synchronizedSet(new HashSet<BluetoothDevice>());
    }
  };

  public void onScanResult(BluetoothDevice device) {
    if (advertisersDuringPeriod != null) {
      advertisersDuringPeriod.add(device);
    }
  }

  public void startListeningToScanResults() {
    // The scan result period is typically 100 milliseconds. (for SensorTags)
    // Sometimes as short as 90 milliseconds, and as long as 500 milliseconds.

    /*
     * When the scan modus is one-off we set the timeout period to be equal to the SensorTag advertisement period. This is a solution, the one-off users (Nexus
     * 7 (2012) users) should get a more buggy experience than the Samsung users.
     * 
     * See the ScanType enum for more details.
     */
    handle = scheduler.scheduleAtFixedRate(runnable, 0, TIME_OUT_PERIOD, TimeUnit.MILLISECONDS);
  }

  public void stopListeningToScanResults() {
    boolean mayInterruptIfRunning = true;
    handle.cancel(mayInterruptIfRunning);
  }
}
