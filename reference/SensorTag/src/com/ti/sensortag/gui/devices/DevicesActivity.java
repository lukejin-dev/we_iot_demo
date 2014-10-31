/**************************************************************************************************
  Filename:       DevicesActivity.java
  Revised:        $Date: 2013-08-30 15:51:02 +0200 (fr, 30 aug 2013) $
  Revision:       $Revision: 27511 $

  Copyright 2013 Texas Instruments Incorporated. All rights reserved.
 
  IMPORTANT: Your use of this Software is limited to those specific rights
  granted under the terms of a software license agreement between the user
  who downloaded the software, his/her employer (which must be your employer)
  and Texas Instruments Incorporated (the "License").  You may not use this
  Software unless you agree to abide by the terms of the License. 
  The License limits your use, and you acknowledge, that the Software may not be 
  modified, copied or distributed unless used solely and exclusively in conjunction 
  with a Texas Instruments Bluetooth� device. Other than for the foregoing purpose, 
  you may not use, reproduce, copy, prepare derivative works of, modify, distribute, 
  perform, display or sell this Software and/or its documentation for any purpose.
 
  YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
  PROVIDED �AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
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
package com.ti.sensortag.gui.devices;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ti.sensortag.R;
import com.ti.sensortag.ble.LeController;
import com.ti.sensortag.models.Devices;

public class DevicesActivity extends ListActivity {
  private static final String TAG = "DevicesActivity";

  // URLs
  private static final Uri URL_FORUM = Uri.parse("http://e2e.ti.com/support/low_power_rf/default.aspx?DCMP=hpa_hpa_community&HQS=NotApplicable+OT+lprf-forum");
  private static final Uri URL_STHOME = Uri.parse("http://www.ti.com/ww/en/wireless_connectivity/sensortag/index.shtml?INTC=SensorTag&HQS=sensortag");

  // Requests to other activities
  private static final int REQ_ENABLE_BT = 0;

  static volatile boolean active = false;
  private volatile boolean bleStarted = false;
  private BluetoothAdapter mBluetoothAdapter = null;
  private DevicesListAdapter deviceListAdapter;
  private IntentFilter mFilter;
  private DevicesActivity mThis = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate");
    super.onCreate(savedInstanceState);

    // Use this check to determine whether BLE is supported on the device. Then
    // you can selectively disable BLE-related features.
    if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
      Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
      finish();
      return;
    }

    // Initializes a Bluetooth deviceListAdapter. For API level 18 and above, get a
    // reference to BluetoothAdapter through BluetoothManager.
    BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
    mBluetoothAdapter = bluetoothManager.getAdapter();

    // Checks if Bluetooth is supported on the device.
    if (mBluetoothAdapter == null) {
      Toast.makeText(this, R.string.bt_not_supported, Toast.LENGTH_LONG).show();
      finish();
      return;
    }

    mThis = this;
    active = false;
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    // This code sets the emptyView of BLE devices to be the view
    // inflated from empty_ble_devices.xml
    // I have no idea how this code does this, but it works.

    LinearLayout v = new LinearLayout(this);
    LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.empty_ble_devices, v);
    v.setVisibility(View.GONE);

    ListView listView = getListView();
    ((ViewGroup) listView.getParent()).addView(v);
    listView.setEmptyView(v);

    // Register the BroadcastReceiver
    mFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);

    // Start the BLE stack
    initApp();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_activity_actions, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle presses on the action bar items
    switch (item.getItemId()) {
    case android.R.id.home:
      onBackPressed();
      return true;
    case R.id.opt_bt:
      onBluetooth();
      break;
    case R.id.opt_e2e:
      onUrl(URL_FORUM);
      break;
    case R.id.opt_sthome:
      onUrl(URL_STHOME);
      break;
    case R.id.opt_about:
      onAbout();
      break;
    case R.id.opt_exit:
      finish();
      break;
    default:
      return super.onOptionsItemSelected(item);
    }
    return true;
  }

  private void onUrl(Uri uri) {
    Intent web = new Intent(Intent.ACTION_VIEW, uri);
    startActivity(web);
  }

  private void onBluetooth() {
    Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
    startActivity(settingsIntent);
  }

  private void onAbout() {
    final Dialog dialog = new AboutDialog(this);
    dialog.show();
  }

  @Override
  public void onResume() {
    Log.d(TAG, "onResume: " + bleStarted);
    super.onResume();

    if (bleStarted) {
      LeController.INSTANCE.shutdownConnection();

      active = LeController.INSTANCE.startScan();
      if (!active)
        Log.e(TAG, "Could not start scan.");
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.d(TAG, "onPause: " + bleStarted);

    if (bleStarted && active) {
      LeController.INSTANCE.stopScan();
      active = false;
    }
  }

  /**
   * NB: onDestroy will be called when a user exits your app during normal use of your app, as expected. But when you reinstall the app during development
   * onDestroy will not be called and you will get weird bugs due to cleanup failure.
   * 
   * I don't know of any programmatic way to ensure the cleanup function onDestroy is called when you reinstall the app, so you will just have to manually make
   * sure you exit the app before reinstall.
   * 
   * Disclaimer: Not an Android expert.
   */
  @Override
  protected void onDestroy() {
    // Stop listening for broadcasts
	  try
	  {
		    unregisterReceiver(mReceiver);
		    mFilter = null;
		    active = false;

		    // Stop listening for updates
		    Devices.INSTANCE.removePropertyChangeListener(deviceListAdapter);

		    // Stop BLE activities
		    LeController.INSTANCE.onDestroy();
		    mBluetoothAdapter = null;
		    deviceListAdapter = null;
		    Log.d(TAG, "onDestroy");
		  
	  }
	  catch (Exception x)
	  {
		  Log.d(TAG, "onDestroy: "+x.toString());
	  }
    super.onDestroy();
  }

  private void startBle() {
    // Prepare UI
    deviceListAdapter = new DevicesListAdapter(this);
    setListAdapter(deviceListAdapter);
    Devices.INSTANCE.addPropertyChangeListener(deviceListAdapter);

    // Start scanning
    bleStarted = true;
    LeController.INSTANCE.run(this);
  }

  private void initApp() {
    // Broadcast receiver
    registerReceiver(mReceiver, mFilter);

    if (mBluetoothAdapter.isEnabled()) {
      // Start straight away
      startBle();
    } else {
      bleStarted = false;
      // Request BT deviceListAdapter to be turned on
      Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableIntent, REQ_ENABLE_BT);
    }
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {

    case REQ_ENABLE_BT:
      // When the request to enable Bluetooth returns
      if (resultCode == Activity.RESULT_OK) {
        Toast.makeText(this, "Bluetooth was turned on ", Toast.LENGTH_SHORT).show();
      } else {
        // User did not enable Bluetooth or an error occurred
        Toast.makeText(this, "Bluetooth was not turned on", Toast.LENGTH_SHORT).show();
        finish();
      }
      break;
    default:
      Log.e(TAG, "Unknown request code");
      break;
    }
  }

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      final String action = intent.getAction();
      Log.d(TAG, action);

      // Adapter state changed
      if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

        Log.d(TAG, "Adapter state: " + mBluetoothAdapter.getState());
        switch (mBluetoothAdapter.getState()) {
        case BluetoothAdapter.STATE_ON:
          startBle();
          break;
        case BluetoothAdapter.STATE_OFF:
          bleStarted = false;
          Toast.makeText(mThis, "Exiting SensorTag application...", Toast.LENGTH_LONG).show();
          finish();
          break;
        default:
          Log.w(TAG, "Action STATE CHANGED not processed ");
          break;
        }
      } else {
        Log.w(TAG, "Action not processed: " + action);
      }

    }
  };

}
