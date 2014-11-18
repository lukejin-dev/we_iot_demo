package ies.iot.iotdemo3;

import ies.iot.demolib.ble.BeaconScanInfo;
import ies.iot.demolib.ble.UIConnectCallback;
import ies.iot.demolib.utils.DemoSettings;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothDevice;

public class ScanActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private SensorTagService mService;
    private ListView mListScan;
    private ScanListAdapter mListScanAdapter;
    private Handler mDisappearCheckHandler;
    private TextView mTextViewStatus;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        mContext = this;
        mTextViewStatus = (TextView)findViewById(R.id.tv_status);
        mListScan = (ListView)findViewById(R.id.lv_scan);
        mListScanAdapter = new ScanListAdapter(this);
        mListScan.setAdapter(mListScanAdapter);
        mListScan.setOnItemClickListener(
                mListScanAdapter.get_item_click_listener());
        
        SharedPreferences setting_preference = 
                PreferenceManager.getDefaultSharedPreferences(mContext); 
        String server_url = setting_preference.getString("report_server_url", 
                "http://69.10.52.93:8400/api/p3/write");
        DemoSettings.getInstance().setServerUrl3(this, server_url);
        String interval = setting_preference.getString("updates_interval", 
                "1000");
        DemoSettings.getInstance().setReportInterval(this, interval);
        
        mDisappearCheckHandler = new Handler();
        mDisappearCheckHandler.postDelayed(mDisappearCheckRunner, 1000);
        
        connectService();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        disconnectService();
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
            mService.registerConnectCallback(mScanConnectCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.v(TAG, "onServiceDisconnected."); 
            Toast.makeText(mContext, "Service disconnected", 
                    Toast.LENGTH_SHORT).show();            
        }
        
    };
    
    public void connectService() {
        Intent intent = new Intent(this, SensorTagService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }
    
    
    public void disconnectService() {
        unbindService(mConnection);
    }
    
    public SensorTagService getService() {
        return mService;
    }
    
    private Handler mScanCallbackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mListScanAdapter.update_device((BluetoothDevice)msg.obj, msg.arg1);
            
        }
    };
    
    private Runnable mDisappearCheckRunner = new Runnable() {
        @Override
        public void run() {
            mListScanAdapter.refresh_disappeared_device();
            mDisappearCheckHandler.postDelayed(mDisappearCheckRunner, 1000);
        }
    };
    
    private UIConnectCallback mScanConnectCallback = new UIConnectCallback() {
        public void onScanCallback(BluetoothDevice device, int rssi) {
            super.onScanCallback(device, rssi);
            mListScanAdapter.update_device(device, rssi);
            mTextViewStatus.setText("Server Errors:" + mService.getReportError());
        }        
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.mi_stop) {
            if (mService != null) {
                mService.stopBle();
            }
            finish();
        } else if (id == R.id.mi_settings) {
            Intent intent = new Intent();
            intent.setClass(this, SettingPreferenceActivity.class);
            startActivity(intent);
            return true;            
        }
        return super.onOptionsItemSelected(item);
    }     
}
