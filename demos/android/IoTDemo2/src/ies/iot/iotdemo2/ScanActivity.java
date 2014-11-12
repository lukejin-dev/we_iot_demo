package ies.iot.iotdemo2;

import ies.iot.demolib.ble.BeaconScanInfo;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;
import android.bluetooth.BluetoothDevice;

public class ScanActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private SensorTagService mService;
    private ListView mListScan;
    private ScanListAdapter mListScanAdapter;
    private Handler mDisappearCheckHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        mContext = this;
        mListScan = (ListView)findViewById(R.id.lv_scan);
        mListScanAdapter = new ScanListAdapter(this);
        mListScan.setAdapter(mListScanAdapter);
        mListScan.setOnItemClickListener(
                mListScanAdapter.get_item_click_listener());
        
        connectService();
        
        mDisappearCheckHandler = new Handler();
        mDisappearCheckHandler.postDelayed(mDisappearCheckRunner, 1000);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisappearCheckHandler.removeCallbacks(mDisappearCheckRunner);
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
            mService.getBleManager().startScan(mScanCallbackHandler);
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
}
