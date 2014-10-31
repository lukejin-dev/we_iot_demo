/**************************************************************************************************
  Filename:       WriteQueue.java
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

import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.util.Log;

/**
 * Class for queuing the BLE write operations writeDescriptor and writeCharacteristc.
 * 
 * This class is needed because you cannot have two writes waiting for callbacks. e.g. you cannot call writeDescriptor, and then writeDescriptor again before
 * the onDescriptorWrite callback is received.
 * */
public class WriteQueue {

  private static final String TAG = "WriteQueue";
  /**
   * The runnable at head is the current runnable waiting for a callback. An empty list implies that no callbacks are expected.
   * */
  private final LinkedList<Runnable> writes = new LinkedList<Runnable>();

  public synchronized void queueRunnable(Runnable write) {
    if (writes.isEmpty()) {
      new Thread(write).start();
    }

    writes.addLast(write);

    Log.v(TAG, "Queue size: " + writes.size());
  }

  /**
   * This method is called when a callback is returned, allowing us to issue a new write.
   * 
   * @throws NoSuchElementException
   *           The queue should never have to be asked to deque if it is empty because elements are only popped after callbacks.
   * */
  public synchronized void issue() {
    if (writes.isEmpty()) {
      Log.w(TAG, "No runnable waiting for callback");
    } else {
      writes.removeFirst();
    }

    if (!writes.isEmpty()) {
      Runnable write = writes.peekFirst();
      new Thread(write).start();
    }
    Log.v(TAG, "Queue size: " + writes.size());
  }

  /**
   * If issue was called for every queue runnable call then the above code would work great. But in the real world callbacks are lost, which will cause all the
   * other runnables waiting in line to wait forever.
   * 
   * Flush is called to cancel all waiting runnables.
   */
  public synchronized void flush() {
    Log.v(TAG, "Flushed queue of size: " + writes.size());
    writes.clear();
  }
}
