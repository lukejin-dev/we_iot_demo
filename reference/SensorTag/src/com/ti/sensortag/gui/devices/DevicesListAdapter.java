/**************************************************************************************************
  Filename:       DeviceListAdapter.java
  Revised:        $Date: 2013-08-30 14:01:47 +0200 (fr, 30 aug 2013) $
  Revision:       $Revision: 27496 $

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

import static com.ti.sensortag.models.Devices.State.SILENT;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ti.sensortag.R;
import com.ti.sensortag.ble.LeController;
import com.ti.sensortag.gui.preferences.PreferencesActivity;
import com.ti.sensortag.gui.preferences.PreferencesFragment;
import com.ti.sensortag.models.Devices;
import com.ti.sensortag.models.Devices.State;

class DevicesListAdapter extends BaseAdapter implements PropertyChangeListener {

  private static final String TAG = "DevicesListAdapter";
  List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
  volatile Map<BluetoothDevice, ViewContainer> containers = new HashMap<BluetoothDevice, ViewContainer>();

  Devices model = Devices.INSTANCE;

  Context context;

  public DevicesListAdapter(Context context) {
    this.context = context;
    initializeFromModel();
  }

  private void initializeFromModel() {
    for (State state : Devices.State.values()) {
      for (BluetoothDevice device : state.getDevices()) {
        devices.add(device);
      }
    }
  }

  @Override
  public int getCount() {
    return devices.size();
  }

  @Override
  public Object getItem(int position) {
    return devices.get(position);
  }

  @Override
  public long getItemId(int position) {
    return devices.get(position).hashCode();
  }

  @Override
  public View getView(int pos, View convertView, ViewGroup parent) {
    BluetoothDevice device = devices.get(pos);
    Log.d(TAG, "position =" + pos);
    if (device == null) {
      Log.e(TAG, "BluetoothDevice is null");
      return null;
    }

    ViewContainer container = new ViewContainer(device, context);

    containers.put(device, container);

    return container.layout;
  }

  @Override
  public void propertyChange(final PropertyChangeEvent event) {
    /*
     * If this activity is not active our program will crash if we try to update the UI.
     */
    if (!DevicesActivity.active)
      return;

    ((Activity) context).runOnUiThread(new Runnable() {
      public void run() {
        String eventName = event.getPropertyName();
        // Check if a new device has been discovered
        if (eventName.equals("NEW_DEVICE_ADVERTISING")) {
          // A device started advertising.
          BluetoothDevice device = (BluetoothDevice) event.getNewValue();
          boolean newDevice = !devices.contains(device);
          if (newDevice) {
            devices.add(device);
            notifyDataSetChanged();
            return;
          }
        }

        if (eventName.equals("NEW_DEVICE_ADVERTISING")) {
          final BluetoothDevice device = (BluetoothDevice) event.getNewValue();
          ViewContainer container = containers.get(device);
          Button button = container.btn;
          button.setText("Connect");
          button.setEnabled(true);
          button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
              final Intent intent = new Intent(context, PreferencesActivity.class);

              /*
               * These two extras cause the preferences activity to expand the headers immediately, as opposed to showing a one-element-list of headers. Which
               * you would need to click to expand and get the actual preferences.
               */
              intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferencesFragment.class.getName());
              intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
              intent.putExtra("com.ti.sensortag.device", device);

              context.startActivity(intent);
            }
          });

          String description = ViewContainer.generateDescription(device, State.ADVERTISING);
          container.descriptionTextView.setText(description);
        } else if (eventName.equals("NEW_DEVICE_SILENT")) {
          BluetoothDevice device = (BluetoothDevice) event.getNewValue();

          ViewContainer container = containers.get(device);
          if (container != null) {
            container.btn.setEnabled(false);
            String description = ViewContainer.generateDescription(device, State.SILENT);
            container.descriptionTextView.setText(description);
          } else {
            Log.e(TAG, "Should be unreachable code.");
          }
        }
      }
    });
  }

  private static class ViewContainer {

    LinearLayout layout;
    Button btn;
    TextView descriptionTextView;

    public ViewContainer(BluetoothDevice device, Context context) {
      State state = Devices.INSTANCE.getStateOfDevice(device);

      ImageView img = createImgView(device, context);
      descriptionTextView = createDescriptionTextView(device, state, context);

      layout = new LinearLayout(context);
      layout.setOrientation(LinearLayout.HORIZONTAL);
      layout.setGravity(Gravity.CENTER);

      Resources r = context.getResources();
      int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, r.getDisplayMetrics());

      layout.setPadding(px, px, px, px);

      if (img != null)
        layout.addView(img);
      layout.addView(descriptionTextView);

      descriptionTextView.setPadding(px, px, px, px);
      descriptionTextView.setTextSize(20);

      switch (state) {
      case ADVERTISING:
        layout.addView(btn = createConnectBtn(device, context));
        break;
      case CONNECTED:
        layout.addView(btn = createDisconnectBtn(device, context));
        break;
      case SILENT:
        layout.addView(btn = createStubBtn(context));
        break;
      }
    }

    static Button createStubBtn(Context context) {
      Button btn = new Button(context);
      btn.setText("Connect");
      btn.setEnabled(false);
      return btn;
    }

    static Button createDisconnectBtn(final BluetoothDevice device, Context context) {
      Button btn = new Button(context);
      btn.setText("Disconnect");
      btn.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          LeController.INSTANCE.shutdownConnection();
        }
      });
      return btn;
    }

    static Button createConnectBtn(final BluetoothDevice device, final Context context) {
      Button btn = new Button(context);
      btn.setText("Connect");
      btn.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
          final Intent intent = new Intent(context, PreferencesActivity.class);

          /*
           * These two extras cause the preferences activity to expand the headers immediately, as opposed to showing a one-element-list of headers. Which you
           * would need to click to expand and get the actual preferences.
           */
          intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferencesFragment.class.getName());
          intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
          intent.putExtra("com.ti.sensortag.device", device);

          context.startActivity(intent);
        }
      });
      return btn;
    }

    private TextView createDescriptionTextView(BluetoothDevice device, State state, Context context) {
      TextView tv = new TextView(context);
      String txt = generateDescription(device, state);
      tv.setText(txt);
      return tv;
    }

    private static String generateDescription(BluetoothDevice device, State state) {
      String txt = device.getName() + "\n";
      txt += device.getAddress() + "\n";
      txt += state == SILENT ? "not advertising" : state.name().toLowerCase();
      return txt;
    }

    private ImageView createImgView(BluetoothDevice device, Context context) {
      int imgId = R.drawable.sensortag;

      if (device != null) {
        String name = device.getName();
        if (name != null) {
          if (!name.equals("SensorTag"))
            // Should not happen
            imgId = R.drawable.unknown;
        } else {
          Log.e(TAG, "Device name: " + name);
        }
      } else {
        Log.e(TAG, "device = null");
      }
      ImageView imgView = new ImageView(context);
      imgView.setImageResource(imgId); // NB: Might cause UI thread to do too much work.
      return imgView;
    }
  }
}
