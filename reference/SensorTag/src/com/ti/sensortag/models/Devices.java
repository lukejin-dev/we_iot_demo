/**************************************************************************************************
  Filename:       Devices.java
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

/**
 * <pre>
 * Model class akin to {@link Measurements}.
 * This Model maintains the state of the devices that are known to exist.
 * A device can exclusively be in one of three states;
 * 
 *  Silent 	- The device has NOT sent out an advertisement in at least x seconds.
 *  Advertising - The device has sent out an advertisement in the past x seconds.
 *  Connected   - The device is currently connected.
 *  
 *  The property change events fired can be in one of three formats:
 *  
 *  1. <State>
 *  2. NEW_DEVICE_<State>
 *  3. LOST_DEVICE_<State>
 *  
 *  Events with the first format send the new and old sets of devices as values in the event.
 *  Events with the second and third format send a single value of type BluetoothDevice.
 *  
 *  This is overly complicated and should be changed to a simple broadcast model
 *  with Messages and Bundles.
 * </pre>
 * */
public enum Devices {
  INSTANCE;

  public enum State {
    SILENT, ADVERTISING, CONNECTED;

    private final Set<BluetoothDevice> devices = new HashSet<BluetoothDevice>();

    /***
     * @returns shallow copy of the devices in this state.
     */
    public Set<BluetoothDevice> getDevices() {
      return new HashSet<BluetoothDevice>(devices);
    }
  };

  private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
  public static final String NEW_DEVICE_ = "NEW_DEVICE_";
  public static final String LOST_DEVICE_ = "LOST_DEVICE_";
  public static final String TAG = "Devices";

  public synchronized void setState(State newState, BluetoothDevice device) {
    if (newState == null || device == null) {
      // That is very weird, callers should not call this method with null.
      Log.e(TAG, "called setState with null argument");
      throw new NullPointerException();
    }

    if (newState.devices.contains(device))
      return; // Nothing to do here if it is already in that state.

    // Note that we make shallow copies of the collections,
    // under no circumstances should an outside class
    // be able to get a reference to one of our three internal collections.

    // These variable names are abominations, but the comments should make them clear.
    Set<BluetoothDevice> oldOldSet, // The set of devices from whence it came, including the device.
    newOldSet, // The set of devices from whence it came, excluding the device.
    oldNewSet, // The set of devices to where it is going, excluding the device.
    newNewSet; // The set of devices to where it is going, including the device.

    State oldState = getStateOfDevice(device);
    if (oldState != null) {
      // The device was already in a state.
      oldOldSet = new HashSet<BluetoothDevice>(oldState.devices);
      oldState.devices.remove(device);
      newOldSet = new HashSet<BluetoothDevice>(oldState.devices);

      changeSupport.firePropertyChange(oldState.name(), oldOldSet, newOldSet);
      changeSupport.firePropertyChange(LOST_DEVICE_ + oldState.name(), null, device);
    }

    oldNewSet = new HashSet<BluetoothDevice>(newState.devices);
    newState.devices.add(device);
    newNewSet = new HashSet<BluetoothDevice>(newState.devices);

    changeSupport.firePropertyChange(newState.name(), oldNewSet, newNewSet);

    // The following change events are for the listeners
    // interested in knowing that a device has entered or left a state.
    changeSupport.firePropertyChange(NEW_DEVICE_ + newState.name(), null, device);
  }

  /**
   * @return state of device, or null if the device is unknown.
   * */
  public synchronized State getStateOfDevice(BluetoothDevice device) {
    for (State state : State.values()) {
      if (state.devices.contains(device)) {
        return state;
      }
    }
    return null;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    // Don't add the same object twice. I can't imagine a use-case where you would want to do that.
    List<PropertyChangeListener> listeners = Arrays.asList(changeSupport.getPropertyChangeListeners());
    if (!listeners.contains(this))
      changeSupport.addPropertyChangeListener(listener);

    String msg = String.format("addPropertyChangeListener(%s); number of listeners is  %d", listener, changeSupport.getPropertyChangeListeners().length);
    Log.i(TAG, msg);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
    String msg = String.format("removePropertyChangeListener(%s); number of listeners is %d", listener, changeSupport.getPropertyChangeListeners().length);
    Log.i(TAG, msg);
  }

  public Set<BluetoothDevice> getAllKnownDevices() {
    Set<BluetoothDevice> allDevices = new HashSet<BluetoothDevice>();
    for (State state : State.values())
      allDevices.addAll(state.getDevices());

    return allDevices;
  }

  // Not necessary to do a shallow copy because changesupport does that for us.
  public PropertyChangeListener[] getPropertyChangeListeners() {
    return changeSupport.getPropertyChangeListeners();
  }
}
