/**************************************************************************************************
  Filename:       PreferencesListener.java
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
package com.ti.sensortag.gui.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.ti.sensortag.ble.Sensor;

/**
 * This class provides the link between gui and ble. When a preference is changed this class tells LeStateMachine to turn on/off a sensor or change the polling
 * time.
 * 
 * We could have had this responsibility in leStateMachine, but it is big enough already.
 * */
public class PreferencesListener implements SharedPreferences.OnSharedPreferenceChangeListener {

  public static final int MAX_SENSORS = 4;

  @SuppressWarnings("unused")
  private static final String TAG = "PreferenceListener";
  private SharedPreferences sharedPreferences;
  private Context context;

  public PreferencesListener(Context context, SharedPreferences sharedPreferences, PreferenceFragment pf) {
    this.context = context;
    this.sharedPreferences = sharedPreferences;
  }

  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Sensor sensor = getSensorFromPrefKey(key);

    boolean noCheckboxWithThatKey = sensor == null;
    if (noCheckboxWithThatKey)
      return;

    boolean turnedOn = sharedPreferences.getBoolean(key, true);

    if (turnedOn && enabledSensors().size() > MAX_SENSORS) {
      alertUserOfLimitWithToast();
      // I tried and failed at enforcing a limit through the gui, a toast will have to do.
    }
  }

  private void alertUserOfLimitWithToast() {
    String text = "Warning: due to unknown reasons (2013-07-25) " + "you cannot subscribe to more than " + MAX_SENSORS
        + " unique sensors during a single connection lifetime.\n";

    Toast.makeText(context, text, Toast.LENGTH_LONG).show();

    // Second part
    String msg = "If you want to try out 4 different sensors you must reconnect.";
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
  }

  /**
   * String is in the format
   * 
   * pref_magnetometer_on
   * 
   * @return Sensor corresponding to checkbox key, or null if there is no corresponding sensor.
   * */
  private Sensor getSensorFromPrefKey(String key) {
    try {
      int start = "pref_".length();
      int end = key.length() - "_on".length();
      String enumName = key.substring(start, end).toUpperCase(Locale.ENGLISH);

      return Sensor.valueOf(enumName);
    } catch (IndexOutOfBoundsException e) {
      // thrown by substring
    } catch (IllegalArgumentException e) {
      // thrown by valueOf
    } catch (NullPointerException e) {
      // thrown by valueOf
    }
    return null; // If exception was thrown while parsing. DON'T replace with catch'em all exception handling.
  }

  private List<Sensor> enabledSensors() {
    List<Sensor> sensors = new ArrayList<Sensor>();
    for (Sensor sensor : Sensor.values())
      if (isEnabledByPrefs(sensor))
        sensors.add(sensor);

    return sensors;
  }

  private boolean isEnabledByPrefs(final Sensor sensor) {
    String preferenceKeyString = "pref_" + sensor.name().toLowerCase(Locale.ENGLISH) + "_on";

    if (!sharedPreferences.contains(preferenceKeyString)) {
      throw new RuntimeException("Programmer error, could not find preference with key " + preferenceKeyString);
    }

    return sharedPreferences.getBoolean(preferenceKeyString, true);
  }
}
