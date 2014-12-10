package ies.iot.demolib.ble;

import java.util.ArrayList;
import java.util.List;

import ies.iot.demolib.sensors.BleSensor;
import ies.iot.demolib.sensors.SensorDb;
import ies.iot.demolib.utils.BleUtil;
import ies.iot.demolib.utils.ServerReporter;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
    public static final int STATE_DISCOVERIED = 5;
    public static final int UPDATE_SENSOR_VALUE = 6;
    public static final int CONTINUE_SCAN_REPORT = 7;
    
    private Context mContext;
    private BluetoothAdapter mBleAdapter;
    private Handler mScanCallbackHandler;
    private Handler mStateHandler;
    private BleScanner mBleScanner;
    private BluetoothDevice mBleDevice;
    private BluetoothGatt mBleGatt;
    private int mState;
    private String mDeviceAddress;
    private List<BleSensor> mSensorList = new ArrayList<BleSensor>();
    
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
        Log.v(TAG, "startScan");
        if (!enableBle()) return false;
        assert(mBleAdapter != null);
        
        mScanCallbackHandler = callbackHandler;
        mBleScanner = new BleScanner();
        mBleScanner.startScan(mScanCallback, mBleAdapter);
        setState(STATE_SCANNING);
        
        return true;
    }
    
    // for iot demo3
    private BluetoothAdapter.LeScanCallback mConitnueScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, 
                final int rssi, final byte[] scanRecord) {
            Log.v(TAG, "Scan : " + device.getName() + " " + 
                device.getAddress());
            
            Message msg = new Message();
            msg.what = CONTINUE_SCAN_REPORT;
            msg.obj = device;
            msg.arg1 = rssi;
            
            mStateHandler.sendMessage(msg);
        }
    };   
    
    // for iot demo3
    public boolean startContinueScan() {
        Log.v(TAG, "startContinueScan");
        if (!enableBle()) return false;
        assert(mBleAdapter != null);
        
        mBleScanner = new BleScanner();
        mBleScanner.startScan(mConitnueScanCallback, mBleAdapter);
        setState(STATE_SCANNING);
        
        return true;
    }
    
    public void stopContinueScan() {
        Log.v(TAG, "stopScan");
        if (mState != STATE_SCANNING) {
            return;
        }
        
        mBleScanner.stopLeScan(mConitnueScanCallback, mBleAdapter);
        setState(STATE_DISCONNECTED);
    }
    
    public void stopScan() {
        Log.v(TAG, "stopScan");
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
        msg.what = state;
        mStateHandler.sendMessage(msg);
    }
    
    public int getState() {
        return mState;
    }
    
    public boolean connectBle(String address) {
        Log.v(TAG, "connectBle");
        if (!enableBle()) return false;
        assert(mBleAdapter != null);
        
        if (mDeviceAddress != null && 
                !mDeviceAddress.equalsIgnoreCase(address)) {
            Log.e(TAG, "Invalid parameters!");
            return false;
        }
        
        if (mBleDevice == null) {
            mBleDevice = mBleAdapter.getRemoteDevice(
                    BleUtil.MACStringToByteArray(address));
            if (mBleDevice == null) {
                Log.e(TAG, "Fail to get remove device " + address);
                return false;
            }
        }
        
        if (mDeviceAddress != null && mBleGatt != null) {
            mBleGatt.connect();
            setState(STATE_CONNECTING);
            return true;
        }
        
        mDeviceAddress = address;
        mBleGatt = mBleDevice.connectGatt(mContext, true, mBleGattCallback);
        setState(STATE_CONNECTING);
        return true;
    }
    
    public void disconnectBle() {
        Log.v(TAG, "disconnectBle");
        if (mBleGatt != null) {
            mBleGatt.disconnect();
            mBleGatt = null;
        }
    }
    
    public void discoveryService() {
        Log.v(TAG, "discoveryService");
        if (mBleGatt != null) {
            mBleGatt.discoverServices();
        } else {
            Log.e(TAG, "Fail to discovery service, since GATT gone.");
        }
    }
    
    public boolean isConnected() {
        return mState == STATE_CONNECTED;
    }
    
    public boolean isConnecting() {
        return mState == STATE_CONNECTING;
    }
    
    private BluetoothGattCallback mBleGattCallback = 
            new BluetoothGattCallback() {
        
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, 
                int newState) {
            Log.i(TAG, "onConnectionStateChange: " + status + " => " + 
                    newState);
            
            if(newState == BluetoothProfile.STATE_CONNECTED){
                mState = STATE_CONNECTED;
                
                Message msg = new Message();
                msg.what = STATE_CONNECTED;
                mStateHandler.sendMessage(msg);
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                mState = STATE_DISCONNECTED;
                
                Message msg = new Message();
                msg.what = STATE_DISCONNECTED;
                mStateHandler.sendMessage(msg);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered, status: " + status);
            
            if(status == BluetoothGatt.GATT_SUCCESS) {
                Message msg = new Message();
                msg.what = STATE_DISCOVERIED;
                mStateHandler.sendMessage(msg);                
            } else {
            }
        }        
        
        @Override
        public void onCharacteristicRead(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status) {
            Log.i(TAG, "onCharacteristicRead, status: " + status);
            
            
            String service_id = characteristic.getService().getUuid().toString();
            String char_id = characteristic.getUuid().toString();
            
            BleSensor sensor = getSensor(service_id);
            if (sensor != null) {
                sensor.onCharacteristicRead(characteristic);
            }                        
        }
        
        @Override
        public void onCharacteristicChanged(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged");
            
            String service_id = characteristic.getService().getUuid().toString();
            String char_id = characteristic.getUuid().toString();
            
            BleSensor sensor = getSensor(service_id);
            if (sensor != null) {
                sensor.onCharacteristicChanged(characteristic);
                
                Message msg = new Message();
                msg.what = UPDATE_SENSOR_VALUE;
                msg.obj = sensor;
                mStateHandler.sendMessage(msg);
            }
        }
        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicWrite: " + status);
        }
        
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                int status) {
            Log.i(TAG, "onDescriptorRead: " + status);

        }
        
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                int status) {
            Log.i(TAG, "onDescriptorWrite: " + status);
        }     
        
        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onReliableWriteCompleted: " + status);
        }        
    };
    
    private BleSensor getSensor(String serviceId) {
        for (BleSensor s:mSensorList) {
            if (s.get_service_uuid().equalsIgnoreCase(serviceId)) {
                return s;
            }
        }
        return null;        
    }
    
    public void updateSensorList() {
        if (mBleGatt == null) return;
        
        for (BluetoothGattService service:mBleGatt.getServices()) {
            BleSensor s = 
                    SensorDb.get(service.getUuid().toString().toLowerCase());
            if (s != null) {
                if (!mSensorList.contains(s)) {
                    Log.v(TAG, "Add new sensor " + s.get_name());
                    mSensorList.add(s);
                }
                s.enable(mBleGatt, true);
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    
                }
            }
        }        
    }
    
    public List<BleSensor> getSensorList() {
        return mSensorList;
    }
}
