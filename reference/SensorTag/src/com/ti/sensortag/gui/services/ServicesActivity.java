/**************************************************************************************************
  Filename:       ServicesActivity.java
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
package com.ti.sensortag.gui.services;

import static com.ti.sensortag.R.drawable.buttonsoffoff;
import static com.ti.sensortag.R.drawable.buttonsoffon;
import static com.ti.sensortag.R.drawable.buttonsonoff;
import static com.ti.sensortag.R.drawable.buttonsonon;
import static com.ti.sensortag.models.Devices.LOST_DEVICE_;
import static com.ti.sensortag.models.Devices.NEW_DEVICE_;
import static com.ti.sensortag.models.Devices.State.CONNECTED;
import static com.ti.sensortag.models.Measurements.PROPERTY_ACCELEROMETER;
import static com.ti.sensortag.models.Measurements.PROPERTY_AMBIENT_TEMPERATURE;
import static com.ti.sensortag.models.Measurements.PROPERTY_GYROSCOPE;
import static com.ti.sensortag.models.Measurements.PROPERTY_HUMIDITY;
import static com.ti.sensortag.models.Measurements.PROPERTY_IR_TEMPERATURE;
import static com.ti.sensortag.models.Measurements.PROPERTY_MAGNETOMETER;
import static com.ti.sensortag.models.Measurements.PROPERTY_SIMPLE_KEYS;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ti.sensortag.R;
import com.ti.sensortag.models.Devices;
import com.ti.sensortag.models.Measurements;
import com.ti.sensortag.models.Point3D;
import com.ti.sensortag.models.SimpleKeysStatus;

public class ServicesActivity extends Activity implements PropertyChangeListener {

  private static final Measurements model = Measurements.INSTANCE;
  private static final char DEGREE_SYM = '\u2103';

  DecimalFormat decimal = new DecimalFormat("+0.00;-0.00");

  volatile boolean b = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.services_browser);

    getActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public void onResume() {
    super.onResume();

    // Setup this view to listen to the model
    // in the traditional MVC pattern.
    model.addPropertyChangeListener(this);

    // Also listen to changes in connection state so
    // we can notify the user with toasts that
    // the device has been disconnected.
    Devices.INSTANCE.addPropertyChangeListener(this);
  }

  @Override
  public void onPause() {
    super.onPause();

    // Stop listening to changes.
    model.removePropertyChangeListener(this);
    Devices.INSTANCE.removePropertyChangeListener(this);
  }

  /**
   * This class listens to changes in the model of sensor values.
   * */
  @Override
  public void propertyChange(final PropertyChangeEvent event) {
    final String property = event.getPropertyName();

    runOnUiThread(new Runnable() {
      public void run() {
        try {
          if (property.equals(PROPERTY_ACCELEROMETER)) {
            // A change in accelerometer data has occured.
            Point3D newValue = (Point3D) event.getNewValue();

            String msg = "X: " + decimal.format(newValue.x) + "g" + "\nY: " + decimal.format(newValue.y) + "g" + "\nZ: " + decimal.format(newValue.z) + "g";

            ((TextView) findViewById(R.id.accelerometerTxt)).setText(msg);
          } else if (property.equals(PROPERTY_AMBIENT_TEMPERATURE)) {
            double newAmbientValue = (Double) event.getNewValue();
            TextView textView = (TextView) findViewById(R.id.ambientTemperatureTxt);
            String formattedText = decimal.format(newAmbientValue) + DEGREE_SYM;
            textView.setText(formattedText);
          } else if (property.equals(PROPERTY_IR_TEMPERATURE)) {
            double newIRValue = (Double) event.getNewValue();
            TextView textView = (TextView) findViewById(R.id.ir_temperature);
            String formattedText = decimal.format(newIRValue) + DEGREE_SYM;
            textView.setText(formattedText);
          } else if (property.equals(PROPERTY_HUMIDITY)) {
            double newHumidity = (Double) event.getNewValue();
            TextView textView = (TextView) findViewById(R.id.humidityTxt);
            String formattedText = decimal.format(newHumidity) + "%rH";
            textView.setText(formattedText);
          } else if (property.equals(PROPERTY_MAGNETOMETER)) {
            Point3D newValue = (Point3D) event.getNewValue();

            String msg = "X: " + decimal.format(newValue.x) + "uT" + "\nY: " + decimal.format(newValue.y) + "uT" + "\nZ: " + decimal.format(newValue.z) + "uT";

            ((TextView) findViewById(R.id.magnetometerTxt)).setText(msg);
          } else if (property.equals(PROPERTY_GYROSCOPE)) {
            Point3D newValue = (Point3D) event.getNewValue();

            String msg = "X: " + decimal.format(newValue.x) + "deg/s" + "\nY: " + decimal.format(newValue.y) + "deg/s" + "\nZ: " + decimal.format(newValue.z)
                + "deg/s";

            ((TextView) findViewById(R.id.gyroscopeTxt)).setText(msg);
          } else if (property.equals(Measurements.PROPERTY_BAROMETER)) {
            Double newValue = (Double) event.getNewValue();

            String msg = new DecimalFormat("+0.0;-0.0").format(newValue / 100) + " hPa";

            ((TextView) findViewById(R.id.barometerTxt)).setText(msg);
          } else if (property.equals(PROPERTY_SIMPLE_KEYS)) {
            SimpleKeysStatus newValue = (SimpleKeysStatus) event.getNewValue();

            final int img;
            switch (newValue) {
            case OFF_OFF:
              img = buttonsoffoff;
              break;
            case OFF_ON:
              img = buttonsoffon;
              break;
            case ON_OFF:
              img = buttonsonoff;
              break;
            case ON_ON:
              img = buttonsonon;
              break;
            default:
              throw new UnsupportedOperationException();
            }

            ((ImageView) findViewById(R.id.buttons)).setImageResource(img);
          } else if (property.equals(LOST_DEVICE_ + CONNECTED)) {
            // A device has been disconnected
            // We notify the user with a toast

            int duration = Toast.LENGTH_SHORT;
            String text = "Lost connection";

            Toast.makeText(ServicesActivity.this, text, duration).show();
            finish();
          } else if (property.equals(NEW_DEVICE_ + CONNECTED)) {
            // A device has been disconnected
            // We notify the user with a toast

            int duration = Toast.LENGTH_SHORT;
            String text = "Established connection";

            Toast.makeText(ServicesActivity.this, text, duration).show();
          }
        } catch (NullPointerException e) {
          e.printStackTrace();
          // Could be that the ServicesFragment is no longer visible
          // But we still receive property change events.
          // referring to the views with findViewById will then return a null.
        }
      }
    });
  }
}
