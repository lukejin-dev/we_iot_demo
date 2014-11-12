package ies.iot.iotdemo2;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SensorTagService extends Service {
    private final String TAG = getClass().getSimpleName();
    
    private final IBinder mServiceBinder = new  SensorTagServiceBinder();
    
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceBinder;
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "onUnbind");
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

}
