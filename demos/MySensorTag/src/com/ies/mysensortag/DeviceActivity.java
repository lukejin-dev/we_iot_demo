package com.ies.mysensortag;

import java.util.List;

import com.ies.mysensortag.R.id;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class DeviceActivity extends Activity {
    
    private final static String TAG_ = 
            DeviceActivity.class.getSimpleName();
    
    private final static String STATUS_DISCONNECTED = "Disconnected";
    private final static String STATUS_CONNECTED   = "Connected";
    private final static String STATUS_DISCOVERIED   = "Discoveried";
    
    private final static int UI_EVENT_UPDATE_DEVICE = 1;
    private final static int UI_EVENT_UPDATE_RSSI = 2;
    private final static int UI_EVENT_UPDATE_SERVICE = 3;
    public final static String BT_DEV_OBJ = "bt_dev_obj";
    
    private TabHost tabhost_device_;
    private BluetoothDevice ble_dev_;
    private BluetoothGatt   ble_gatt_;
    private BluetoothGattCharacteristic ble_gatt_char_;
    private ExpandableListView listview_services_; 
    private ServiceListAdapter service_list_adapter_;
    
    private static String status_ = STATUS_DISCONNECTED;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_main);

        setup_ui();
        
        if (getIntent().getExtras() == null) {
            Log.e(TAG_, "No bluetooth device selected or found.");
            finish();
        }
        
        ble_dev_ = getIntent().getExtras().getParcelable(BT_DEV_OBJ);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_, "onResume");
        
        Log.d(TAG_, "Associate to bluetooth device: " + ble_dev_);
        ble_gatt_ = ble_dev_.connectGatt(this, true, gatt_callback_);
        if (ble_gatt_ == null) {
            Log.e(TAG_, "Fail to connect bluetooth service!");
            finish();
        }
        
        rssi_refresh_runnable_.run();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG_, "onPause");
        
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG_, "onStop");
        
        if (ble_gatt_ != null) {
            ble_gatt_.close();
            ble_gatt_ = null;
        }
    }
    
    private void setup_ui() {
        //
        // Configure TAB host control
        //
        tabhost_device_ = (TabHost) findViewById(android.R.id.tabhost);
        tabhost_device_.setup();
        
        create_tab(R.id.tab_detail, "detail", "Detail");
        create_tab(R.id.tab_sensor, "sensor", "Sensor");
        
        //
        // Setup service list view
        //
        listview_services_ = (ExpandableListView) findViewById(id.listview_services);
        service_list_adapter_ = new ServiceListAdapter(this);
        listview_services_.setAdapter(service_list_adapter_);
        
        
    }
    
    private void create_tab(int layout_id, String tag, String label) {
        TabSpec spec = tabhost_device_.newTabSpec(tag);
        spec.setIndicator(label);
        spec.setContent(layout_id);
        tabhost_device_.addTab(spec);
    }
    
    private Handler ui_event_handler_ = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            if (msg.what == UI_EVENT_UPDATE_DEVICE) {
                update_ui_detail_device_info();
            } else if (msg.what == UI_EVENT_UPDATE_RSSI) {
                update_ui_detail_rssi(msg.arg1);
            } else if (msg.what == UI_EVENT_UPDATE_SERVICE) {
                update_ui_detail_service();
            }
        }
    };
    
    private Handler rssi_refresh_handler_ = new Handler();
    private Runnable rssi_refresh_runnable_ = new Runnable() {
        @Override
        public void run() {
            if (status_ != STATUS_DISCONNECTED && ble_gatt_ != null) {
                ble_gatt_.readRemoteRssi();
                rssi_refresh_handler_.postDelayed(rssi_refresh_runnable_, 1000);
            }
        }
    };
   
    private void update_ui_detail_device_info() {
        TextView tv_name = (TextView)findViewById(R.id.textview_name);
        tv_name.setText(ble_dev_.getName());
        
        TextView tv_address = (TextView)findViewById(R.id.textview_mac);
        tv_address.setText(ble_dev_.getAddress());
    }
    
    private void update_ui_detail_rssi(int rssi) {
        TextView tv_rssi = (TextView)findViewById(R.id.textview_rssi);
        tv_rssi.setText("" + rssi);
    }
    
    private void update_ui_detail_service() {
        service_list_adapter_.set_list(ble_gatt_.getServices());
    }
    
    private BluetoothGattCallback gatt_callback_ = 
            new BluetoothGattCallback() {
        
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Message msg = new Message();
            msg.what = UI_EVENT_UPDATE_RSSI;
            msg.arg1 = rssi;
            ui_event_handler_.sendMessage(msg);
        }
        
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, 
                int newState) {
            Log.d(TAG_, "onConnectionStateChange: " + status + " => " + 
                    newState);
            
            if(newState == BluetoothProfile.STATE_CONNECTED){
                status_ = STATUS_CONNECTED;
                
                //
                // Update UI
                //
                Message msg = new Message();
                msg.what = UI_EVENT_UPDATE_DEVICE;
                ui_event_handler_.sendMessage(msg);

                //
                // Start runnable for RSSI refreshing
                //
                rssi_refresh_runnable_.run();
                
                //
                // Discovery the service
                //
                ble_gatt_.discoverServices();
                
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                status_ = STATUS_DISCONNECTED;
                finish();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG_, "onServicesDiscovered, status: " + status);
            
            if(status == BluetoothGatt.GATT_SUCCESS) {
                //
                // Update UI
                //
                Message msg = new Message();
                msg.what = UI_EVENT_UPDATE_SERVICE;
                ui_event_handler_.sendMessage(msg);
            } else {
            }
        }        
        
        @Override
        public void onCharacteristicRead(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status) {
            Log.d(TAG_, "onCharacteristicRead, status: " + status);
            
            if (status == BluetoothGatt.GATT_SUCCESS) {
              
            }
        }
        
        @Override
        public void onCharacteristicChanged(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            Log.d(TAG_, "onCharacteristicChanged");
        }
        
    };    
}
