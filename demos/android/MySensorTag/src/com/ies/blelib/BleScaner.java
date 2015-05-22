package com.ies.blelib;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.os.Build;
import android.util.Log;

public class BleScaner {

    private final static String TAG = BleScaner.class.getSimpleName();
    private ScheduledFuture mHandle;
    private final ScheduledExecutorService scheduler = 
            Executors.newScheduledThreadPool(1);
    private int mPeriod = 300;
    
    public enum SCAN_TYPE {
        CONTINUOUS, ONE_OFF
    };
    
    public static SCAN_TYPE getScanType() {
        // Some models provide only one scanResult per scan (Nexus 4, Nexus 7 2013)
        SCAN_TYPE scanType = SCAN_TYPE.CONTINUOUS;

        if (Build.MODEL.equals("Nexus 4"))
          scanType = SCAN_TYPE.ONE_OFF;

        // Nexus 7 (2013)
        if (Build.MODEL.equals("Nexus 7")) //&& Build.DISPLAY.equals("JSS15J"))
          scanType = SCAN_TYPE.ONE_OFF;

        if (Build.MODEL.equals("Lenovo A320t"))
            scanType = SCAN_TYPE.ONE_OFF;
        
        return scanType;
    }
    
    private SCAN_TYPE mScanType = getScanType();
    private boolean mIsFirstRun;
    
    public void startScan(final LeScanCallback callback, 
            final BluetoothAdapter bluetoothAdapter) {
        if (mScanType == SCAN_TYPE.CONTINUOUS) {
            bluetoothAdapter.startLeScan(callback);
            return;
        }

        mIsFirstRun = true;
        final Runnable restartScanRunnable = new Runnable() {
            public void run() {
                try {
                    if (mIsFirstRun) {
                        mIsFirstRun = false;
                        bluetoothAdapter.startLeScan(callback); 
                    } else {
                        Log.v(TAG, "restart scan!");
                        bluetoothAdapter.stopLeScan(callback);
                        bluetoothAdapter.startLeScan(callback);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }
            };
        };

        // restartScanRunnable will be run on the thread that calls
        // scheduleAtFixedRate,
        // which is why we want to create a new thread just for that call.
        new Thread(new Runnable() {
            public void run() {
                mHandle = scheduler.scheduleAtFixedRate(
                      restartScanRunnable, mPeriod, mPeriod, 
                      TimeUnit.MILLISECONDS);
            }
        }).start();
    }

    
    public void stopLeScan(LeScanCallback leScanCallback, 
            BluetoothAdapter bluetoothAdapter) {
        if (mScanType == SCAN_TYPE.CONTINUOUS) {
            bluetoothAdapter.stopLeScan(leScanCallback);
            return;
        }
        
        boolean mayInterruptIfRunning = true;
        mHandle.cancel(mayInterruptIfRunning);
        bluetoothAdapter.stopLeScan(leScanCallback);
    }
}
