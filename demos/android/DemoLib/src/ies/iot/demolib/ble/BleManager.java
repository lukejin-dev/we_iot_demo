package ies.iot.demolib.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BleManager {
    private final String TAG = getClass().getSimpleName();
    
    public static final int STATE_DISCONNECTED = 1;
    public static final int STATE_SCANNING = 2;
    public static final int STATE_CONNECTING = 3;
    public static final int STATE_CONNECTED = 4;
    
    private Context mContext;
    private BluetoothAdapter mBleAdapter;
    private Handler mScanCallbackHandler;
    private Handler mStateHandler;
    private BleScanner mBleScanner;
    private int mState;
    
    public BleManager(Context context, Handler stateHandler) {
        mContext = context;
        mStateHandler = stateHandler;
        mBleScanner = new BleScanner();
    }
    
    private boolean enableBle() {
        if (mBleAdapter != null && mBleAdapter.isEnabled()) {
            return true;
        }
        
        if (mBleAdapter == null) {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) mContext.getSystemService(
                            Context.BLUETOOTH_SERVICE);
            mBleAdapter = bluetoothManager.getAdapter(); 
            if (mBleAdapter == null) {
                return false;
            }
        }
        
        return mBleAdapter.enable();
    }
    
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, 
                final int rssi, final byte[] scanRecord) {
            Log.v(TAG, "Scan : " + device.getName() + " " + 
                device.getAddress());
            
            Message msg = new Message();
            msg.obj = device;
            msg.arg1 = rssi;
            
            if (mScanCallbackHandler != null) {
                mScanCallbackHandler.sendMessage(msg);
            }
        }
    };    
    
    public boolean startScan(Handler callbackHandler) {
        if (!enableBle()) return false;
        assert(mBleAdapter != null);
        
        mScanCallbackHandler = callbackHandler;
        mBleScanner = new BleScanner();
        mBleScanner.startScan(mScanCallback, mBleAdapter);
        setState(STATE_SCANNING);
        
        return true;
    }
    
    public void stopScan() {
        if (mState != STATE_SCANNING) {
            return;
        }
        
        assert(mBleAdapter != null);
        mScanCallbackHandler = null;
        mBleScanner.stopLeScan(mScanCallback, mBleAdapter);
        setState(STATE_DISCONNECTED);
    }
    
    public void setState(int state) {
        Log.v(TAG, "State: " + mState + " => " + state);
        mState = state;
        
        Message msg = new Message();
        msg.arg1 = state;
        mStateHandler.sendMessage(msg);
    }
    
    public int getState() {
        return mState;
    }
}
