/**************************************************************************************************
  Filename:       Measurements.java
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
package com.ti.sensortag.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

public class Measurements {
	public static Measurements INSTANCE = new Measurements();

	private Measurements() {

	}

  public final static String TAG = "Measurements";
  public static int MAX_ELEMENTS = 1000;
  public final static String MEASURE_PROPERTY = "MEASURE_PROPERTY";
  public final static String PROPERTY_ACCELEROMETER = "ACCELEROMETER", PROPERTY_AMBIENT_TEMPERATURE = "AMBIENT", PROPERTY_IR_TEMPERATURE = "IR_TEMPERATURE",
      PROPERTY_HUMIDITY = "HUMIDITY", PROPERTY_MAGNETOMETER = "MAGNETOMETER", PROPERTY_GYROSCOPE = "GYROSCOPE", PROPERTY_SIMPLE_KEYS = "SIMPLE_KEYS",
      PROPERTY_BAROMETER = "BAROMETER";

  private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

  // Model values
  private SimpleKeysStatus status;
  private double ambientTemperature, irTemperature, humidity, barometer;
  private Point3D accelerometer, magnetometer, gyroscope;

  
  
	private LimitedQueue<Double> ambientTemperatureElements = new LimitedQueue<Double>(
			MAX_ELEMENTS);
	private LimitedQueue<Double> irTemperatureElements = new LimitedQueue<Double>(
			MAX_ELEMENTS);
	private LimitedQueue<Double> humidityElements = new LimitedQueue<Double>(
			MAX_ELEMENTS);
	private LimitedQueue<Double> barometerElements = new LimitedQueue<Double>(
			MAX_ELEMENTS);
	private LimitedQueue<Point3D> accelerometerElements = new LimitedQueue<Point3D>(
			MAX_ELEMENTS);
	private LimitedQueue<Point3D> magnetometerElements = new LimitedQueue<Point3D>(
			MAX_ELEMENTS);
	private LimitedQueue<Point3D> gyroscopeElements = new LimitedQueue<Point3D>(
			MAX_ELEMENTS);

  
  
  //TODO: add support for addPropertyChangeListener(propertyName, listener);
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    //Don't add the same object twice. I can't imagine a use-case where you would want to do that.
    List<PropertyChangeListener> listeners = Arrays.asList(changeSupport.getPropertyChangeListeners());
    if (!listeners.contains(this))
      changeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  public void setAmbientTemperature(double newValue) {
    double oldValue = ambientTemperature;
    ambientTemperature = newValue;
    ambientTemperatureElements.add(newValue);
    changeSupport.firePropertyChange(PROPERTY_AMBIENT_TEMPERATURE, oldValue, newValue);
  }

  public void setTargetTemperature(double newValue) {
    double oldValue = irTemperature;
    irTemperature = newValue;
    irTemperatureElements.add(newValue);
    changeSupport.firePropertyChange(PROPERTY_IR_TEMPERATURE, oldValue, newValue);
  }

  public void setAccelerometer(double x, double y, double z) {
    Point3D newValue = new Point3D(x, y, z);
    Point3D oldValue = accelerometer;
    accelerometer = newValue;
    accelerometerElements.add(newValue);
    changeSupport.firePropertyChange(PROPERTY_ACCELEROMETER, oldValue, newValue);
  }

  public void setHumidity(double newValue) {
    double oldValue = humidity;
    humidity = newValue;
    humidityElements.add(newValue);
    changeSupport.firePropertyChange(PROPERTY_HUMIDITY, oldValue, newValue);
  }

  public void setMagnetometer(double x, double y, double z) {
    Point3D newValue = new Point3D(x, y, z);
    Point3D oldValue = magnetometer;
    magnetometer = newValue;
    magnetometerElements.add(newValue);
    changeSupport.firePropertyChange(PROPERTY_MAGNETOMETER, oldValue, newValue);
  }

  public void setGyroscope(float x, float y, float z) {
    Point3D newValue = new Point3D(x, y, z);
    Point3D oldValue = gyroscope;
    gyroscope = newValue;
    gyroscopeElements.add(newValue);
    changeSupport.firePropertyChange(PROPERTY_GYROSCOPE, oldValue, newValue);
  }

  public void setSimpleKeysStatus(SimpleKeysStatus newValue) {
    SimpleKeysStatus oldValue = status;
    status = newValue;
    changeSupport.firePropertyChange(PROPERTY_SIMPLE_KEYS, oldValue, newValue);
  }

  public SimpleKeysStatus getStatus() {
    return status;
  }

  public double getIrTemperature() {
    return irTemperature;
  }

  public double getAmbientTemperature() {
    return ambientTemperature;
  }

  public Point3D getAccelerometer() {
    return accelerometer;
  }

  public void setBarometer(Double newValue) {
    Double oldValue = barometer;
    barometer = newValue;
    barometerElements.add(newValue);
    changeSupport.firePropertyChange(PROPERTY_BAROMETER, oldValue, newValue);

    Log.i(TAG, "setBarometer(" + newValue + ");");
  }

  public LimitedQueue<Double> getAmbientTemperatureElements() {
		return ambientTemperatureElements;
	}

	public LimitedQueue<Double> getIrTemperatureElements() {
		return irTemperatureElements;
	}

	public LimitedQueue<Double> getHumidityElements() {
		return humidityElements;
	}

	public LimitedQueue<Double> getBarometerElements() {
		return barometerElements;
	}

	public LimitedQueue<Point3D> getAccelerometerElements() {
		return accelerometerElements;
	}

	public LimitedQueue<Point3D> getMagnetometerElements() {
		return magnetometerElements;
	}

	public LimitedQueue<Point3D> getGyroscopeElements() {
		return gyroscopeElements;
	}

}
