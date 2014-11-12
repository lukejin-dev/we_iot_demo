package ies.iot.iotdemo2;

import ies.iot.demolib.utils.DemoSettings;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    
    private String mDeviceName;
    private String mDeviceAddress;
    private TextView mTextViewDeviceInfo;
    private Context mContext;
    private SensorTagService mService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        setContentView(R.layout.dashboard);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        mContext = this;
        
        mDeviceName = DemoSettings.getInstance().getDeviceName(this);
        mDeviceAddress = DemoSettings.getInstance().getDeviceAddress(this);
        if (mDeviceName == null || mDeviceAddress == null) {
            Toast.makeText(this, "No device found, please choice from scan", 
                    Toast.LENGTH_SHORT).show();
            backToScan();
        }
        
        mTextViewDeviceInfo = (TextView)findViewById(R.id.tv_device_info);
        mTextViewDeviceInfo.setText(mDeviceName + "\n" + mDeviceAddress);
        
        connectService();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectService();
    }
    
    public void onButtonStopClicked(View view) {
        Log.v(TAG, "onButtonClickStop");
        mService.stopBle();
        backToScan();
    }
    
    public void backToScan() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
        finish();
    }
    
    public void connectService() {
        Intent intent = new Intent(this, SensorTagService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }
    
    public void disconnectService() {
        unbindService(mConnection);
    }   
    
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.v(TAG, "onServiceConnected.");
            Toast.makeText(mContext, "Service connected", 
                    Toast.LENGTH_SHORT).show();
            SensorTagService.SensorTagServiceBinder b = 
                    (SensorTagService.SensorTagServiceBinder)binder;
            mService = b.getService();
            mService.startBle(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected."); 
            Toast.makeText(mContext, "Service disconnected", 
                    Toast.LENGTH_SHORT).show();            
        }
        
    };    
}
