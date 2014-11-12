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

public class SensorTagService extends Service {
    private final String TAG = getClass().getSimpleName();
    private BleManager mBleManager;
    private final IBinder mServiceBinder = new  SensorTagServiceBinder();
    private NotificationCompat.Builder mNotificationBuilder;
   
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
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind");
        if (mBleManager.getState() == BleManager.STATE_SCANNING) {
            mBleManager.stopScan();
        }
        
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
       return START_STICKY;
    }
    
    public BleManager getBleManager() {
        return mBleManager;
    }
    
    public void setNotification(String message) {
        mNotificationBuilder.setContentText(message);
        NotificationManager notificationManager = 
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, mNotificationBuilder.build());        
    }

    private Handler mStateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int state = msg.arg1;
            if (state == BleManager.STATE_SCANNING) {
                setNotification("BLE scanning");
            } else if (state == BleManager.STATE_DISCONNECTED) {
                setNotification("BLE disconnected");
            } else if (state == BleManager.STATE_CONNECTING) {
                setNotification("BLE connecting");
            } else if (state == BleManager.STATE_CONNECTED) {
                setNotification("BLE connected");
            }
        }
    };
    
}
