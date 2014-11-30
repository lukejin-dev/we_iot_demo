package ies.iot.iotdemo2;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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
    private ServerReporter mReporter;
    private String mDeviceAddress;
    private boolean doesShowNotification = true;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
        
        mBleManager = new BleManager(this, mStateHandler);
        mNotificationBuilder = new NotificationCompat.Builder(this)
            .setContentTitle("IoTDemo2")
            .setSmallIcon(R.drawable.ic_launcher)
            .setOngoing(true);
        
        setNotification("Service is starting");
        
        mReporter = new ServerReporter(DemoSettings.getInstance().
                getServerUrl(this));

    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestory");
        doesShowNotification = false;
        stopBle();
        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind");
        if (mBleManager.getState() == BleManager.STATE_SCANNING) {
            mBleManager.stopScan();
        }
        
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
               getServerUrl(this));
       mReporter.set_report_interval(
               Integer.parseInt(DemoSettings.getInstance().
                       getReportInterval(this)));

       
       if (!startBle()) {
           setNotification("Fail to start BLE");
       }
      
       return START_STICKY;
    }
    
    public BleManager getBleManager() {
        return mBleManager;
    }
    
    public void setNotification(String message) {
        if (doesShowNotification) {
            mNotificationBuilder.setContentText(message);
            NotificationManager notificationManager = 
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, 
                    mNotificationBuilder.build());
        }
    }

    private Handler mStateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int state = msg.what;
            if (state == BleManager.STATE_SCANNING) {
                setNotification("BLE scanning");
            } else if (state == BleManager.STATE_DISCONNECTED) {
                setNotification("BLE disconnected");
                if (mUIConnectCallback != null) {
                    mUIConnectCallback.onDisconnected();
                }
            } else if (state == BleManager.STATE_CONNECTING) {
                setNotification("BLE connecting");
            } else if (state == BleManager.STATE_CONNECTED) {
                mBleManager.discoveryService();
                if (mUIConnectCallback != null) {
                    mUIConnectCallback.onConnected();
                }
                setNotification("BLE connected");
            } else if (state == BleManager.STATE_DISCOVERIED) {
                mBleManager.updateSensorList();
                if (mUIConnectCallback != null) {
                    mUIConnectCallback.onServiceDiscoveried();
                }                
                setNotification("BLE connected and discoveried");
            } else if (state == BleManager.UPDATE_SENSOR_VALUE) {
                if (mUIConnectCallback != null) {
                    mUIConnectCallback.onUpdateSensorValue();
                }
                
                TiSensor sensor = (TiSensor)msg.obj;
                mReporter.report_sensor_data(sensor, mDeviceAddress, 
                        sensor.get_service_uuid());
            }
        }
    };
    
    public void registerConnectCallback(UIConnectCallback callback) {
        mUIConnectCallback = callback;
    }
    
    public void unregisterConnectCallback() {
        mUIConnectCallback = null;
    }
    
    public boolean startBle() {
        Log.v(TAG, "startBle: current state:" + mBleManager.getState());
        mDeviceAddress = DemoSettings.getInstance().getDeviceAddress(this);
        if (mDeviceAddress == null) {
            return false;
        }
        
        if (mBleManager.isConnected()) {
            Log.v(TAG, "already connected!");
            if (mUIConnectCallback != null) {
                mUIConnectCallback.onConnected();
            }
            return true;
        }
        
        if (mBleManager.isConnecting()) {
            Log.v(TAG, "is connecting!");
            return true;
        }
        
        return mBleManager.connectBle(mDeviceAddress);
    }
    
    public void stopBle() {
        mBleManager.disconnectBle();
        DemoSettings.getInstance().setDeviceAddress(this, "");
        DemoSettings.getInstance().setDeviceName(this, "");
    }
    
    public int getReportError() {
        return mReporter.get_server_errors();
    }
}
