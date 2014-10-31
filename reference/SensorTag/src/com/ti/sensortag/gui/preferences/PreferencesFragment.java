/**************************************************************************************************
  Filename:       PreferencesFragment.java
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.ti.sensortag.R;
import com.ti.sensortag.ble.LeController;
import com.ti.sensortag.ble.Sensor;
import com.ti.sensortag.gui.services.ServicesActivity;
import com.ti.sensortag.models.Devices;

public class PreferencesFragment extends PreferenceFragment implements PropertyChangeListener {

  private PreferencesListener preferencesListener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    Preference button = (Preference) findPreference("button");
    button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference arg0) {
        Toast.makeText(getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();
        BluetoothDevice device = ((PreferencesActivity) getActivity()).getDevice();
        LeController.INSTANCE.connect(device);
        return true;
      }
    });

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    preferencesListener = new PreferencesListener(getActivity(), prefs, this);
    prefs.registerOnSharedPreferenceChangeListener(preferencesListener);
  }

  @Override
  public void onResume() {
    super.onResume();
    /*
     * In this fragment you can turn on new sensors. Due to the "max-4-unique-sensors-for-notfications-during-a-connection-lifetime" issue you need to reconnect
     * if youy want new sensors. This is why we disonnect when this fragment loads.
     */
    LeController.INSTANCE.shutdownConnection();
    Devices.INSTANCE.addPropertyChangeListener(this);
  }

  @Override
  public void onPause() {
    super.onPause();

    Devices.INSTANCE.removePropertyChangeListener(this);
  }

  public boolean isEnabledByPrefs(final Sensor sensor) {
    String preferenceKeyString = "pref_" + sensor.name().toLowerCase(Locale.ENGLISH) + "_on";

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

    if (!prefs.contains(preferenceKeyString)) {
      throw new RuntimeException("Programmer error, could not find preference with key " + preferenceKeyString);
    }

    return prefs.getBoolean(preferenceKeyString, true);
  }

  @Override
  public void propertyChange(PropertyChangeEvent event) {
    if (event.getPropertyName().equals("NEW_DEVICE_CONNECTED")) {
      Activity activity = getActivity();

      final Intent intent = new Intent(activity, ServicesActivity.class);
      activity.startActivity(intent);
    }
  }

  /*
   * Using PreferencesFragment as a selection screen is a hack. Getting a button into the layout forces us to override the layout in preferences.xml this manual
   * overriding could cause poor portability between vastly different screen sizes.
   * 
   * Alas, this is the simplest solution to what we want at the moment.
   */
}
