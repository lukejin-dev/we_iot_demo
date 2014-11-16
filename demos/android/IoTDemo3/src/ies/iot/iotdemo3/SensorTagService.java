package ies.iot.iotdemo3;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import ies.iot.demolib.ble.*;
import ies.iot.demolib.sensors.BleSensor;
import ies.iot.demolib.sensors.TiSensor;
import ies.iot.demolib.utils.*;

public class SensorTagService extends Service {
    private final String TAG = getClass().getSimpleName();
    private BleManager mBleManager;
    private final IBinder mServiceBinder = new  SensorTagServiceBinder();
    private NotificationCompat.Builder mNotificationBuilder;
    private UIConnectCallback mUIConnectCallback;
    private int NOTIFICATION_ID = 222;
    private ServerReporter3 mReporter;
    private String mDeviceAddress;
    private int interval = 1000;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
        
        mBleManager = new BleManager(this, mStateHandler);
        mNotificationBuilder = new NotificationCompat.Builder(this)
            .setContentTitle("IoTDemo3")
            .setSmallIcon(R.drawable.ic_launcher)
            .setOngoing(true);
        
        setNotification("Service is starting");

        String android_id = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID); 
        Log.v(TAG, "!!!!!!! " + DemoSettings.getInstance().
                getServerUrl3(this));
        mReporter = new ServerReporter3(DemoSettings.getInstance().
                getServerUrl3(this), android_id);
        Log.v(TAG, "android ID: " + android_id);
        mReporter.set_report_interval(10000);
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestory");
        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        
        mBleManager.stopContinueScan();
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind");
        unregisterConnectCallback();
        return true;
    }
    
    public class SensorTagServiceBinder extends Binder {
        public SensorTagService getService() {
            return SensorTagService.this;
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Let it continue running until it is stopped.
       Log.v(TAG, "onStartCommand");
       
       mReporter.set_server_address(DemoSettings.getInstance().
               getServerUrl3(this));
       mReporter.set_report_interval(
               Integer.parseInt(DemoSettings.getInstance().
                       getReportInterval(this)));

       mBleManager.startContinueScan();
       
       return START_STICKY;
    }
    
    public BleManager getBleManager() {
        return mBleManager;
    }
    
    public void setNotification(String message) {
        mNotificationBuilder.setContentText(message);
        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, 
                mNotificationBuilder.build());        
    }

    private Handler mStateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int state = msg.what;
            if (state == BleManager.STATE_SCANNING) {
                setNotification("BLE scanning");
            } else if (state == BleManager.CONTINUE_SCAN_REPORT) {
                int rssi = msg.arg1;
                BluetoothDevice device = (BluetoothDevice) msg.obj;
                
                if (mUIConnectCallback != null) {
                    mUIConnectCallback.onScanCallback(
                            device,
                            rssi);
                }
                mReporter.report_sensor_rssi(device.getAddress(), rssi);
            }
        }
    };
    
    public void registerConnectCallback(UIConnectCallback callback) {
        mUIConnectCallback = callback;
    }
    
    public void unregisterConnectCallback() {
        mUIConnectCallback = null;
    }

    
    public int getReportError() {
        return mReporter.get_server_errors();
    }
}
