package com.ies.mysensortag;

import java.util.ArrayList;
import java.util.List;

import com.ies.blelib.sensor.BleSensor;
import com.ies.blelib.sensor.SensorDb;
import com.ies.blelib.sensor.TiSensor;
import com.ies.mysensortag.R.id;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.ToggleButton;

public class DeviceActivity extends Activity {
    
    private final static String TAG_ = 
            DeviceActivity.class.getSimpleName();
    
    private final static String STATUS_DISCONNECTED = "Disconnected";
    private final static String STATUS_CONNECTED   = "Connected";
    private final static String STATUS_DISCOVERIED   = "Discoveried";
    
    private final static int UI_EVENT_UPDATE_DEVICE = 1;
    private final static int UI_EVENT_UPDATE_RSSI = 2;
    private final static int UI_EVENT_UPDATE_SERVICE = 3;
    private final static int UI_EVENT_UPDATE_SENSOR_VALUE = 4;
    
    public final static String BT_DEV_OBJ = "bt_dev_obj";
    
    private TabHost tabhost_device_;
    private BluetoothDevice ble_dev_;
    private BluetoothGatt   ble_gatt_;
    private BluetoothGattCharacteristic ble_gatt_char_;
    private ExpandableListView listview_services_; 
    private ListView listview_sensors_;
    private ServiceListAdapter service_list_adapter_;
    private SensorListAdapter sensor_list_adapter_;
    private ServerReporter reporter_;
    private ProgressDialog dlg_progress_;
    private Context context_;
    private ToggleButton button_report_server_;
    private static String status_ = STATUS_DISCONNECTED;
    private TextView textview_server_errors_;
    
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

        context_ = this;
        
        Log.d(TAG_, "Associate to bluetooth device: " + ble_dev_);
        ble_gatt_ = ble_dev_.connectGatt(this, true, gatt_callback_);
        if (ble_gatt_ == null) {
            Log.e(TAG_, "Fail to connect bluetooth service!");
            finish();
        }
        
        rssi_refresh_runnable_.run();

        SharedPreferences setting_preference = 
                PreferenceManager.getDefaultSharedPreferences(this); 

        reporter_ = new ServerReporter(
                setting_preference.getString("report_server_url", ""));
        dlg_progress_ = ProgressDialog.show(context_, 
                "Connect to device", 
                "Connecting bluetooth");
        
        button_report_server_ = (ToggleButton) 
                findViewById(R.id.bt_report_server);
        
        textview_server_errors_ = (TextView)
                findViewById(R.id.textview_server_errors);
    }

    public BluetoothGatt get_gatt() {
        return ble_gatt_;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_, "onResume");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG_, "onPause");
        
    }
    
    @Override
    protected void onDestroy() {
        super.onStop();
        Log.d(TAG_, "onDestroy");
        
        if (ble_gatt_ != null) {
            ble_gatt_.disconnect();
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
        
        //
        // Setup sensor list view
        //
        listview_sensors_ = (ListView) findViewById(id.listview_sensors);
        sensor_list_adapter_ = new SensorListAdapter(this);
        listview_sensors_.setAdapter(sensor_list_adapter_);
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
                
                BluetoothGatt gatt = (BluetoothGatt)msg.obj;
                gatt.discoverServices();
                
                if (dlg_progress_ != null) {
                    dlg_progress_.dismiss();
                    dlg_progress_ = null;
                }
                dlg_progress_ = ProgressDialog.show(context_, 
                            "Connect to device", 
                            "Retrieve bluetooth services");
            } else if (msg.what == UI_EVENT_UPDATE_RSSI) {
                update_ui_detail_rssi(msg.arg1);
            } else if (msg.what == UI_EVENT_UPDATE_SERVICE) {
                update_ui_detail_service();
                update_ui_sensors();
                
                if (dlg_progress_ != null) {
                    dlg_progress_.dismiss();
                }
            } else if (msg.what == UI_EVENT_UPDATE_SENSOR_VALUE) {
                sensor_list_adapter_.notifyDataSetChanged();
                TiSensor sensor = (TiSensor)msg.obj;
                if (button_report_server_.isChecked()) {
                    reporter_.report_sensor_data(
                            sensor,
                            ble_gatt_.getDevice().getAddress(),
                            sensor.get_service_uuid(),
                            sensor.get_json_string());
                    set_server_errors(reporter_.get_server_errors());
                }
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
        if (ble_gatt_ == null) {
            return;
        }
        
        TextView tv_rssi = (TextView)findViewById(R.id.textview_rssi);
        tv_rssi.setText("" + rssi);
    }
    
    private void update_ui_detail_service() {
        if (ble_gatt_ == null) {
            return;
        }
        service_list_adapter_.set_list(ble_gatt_.getServices());
    }
    
    private void update_ui_sensors() {
        if (ble_gatt_ == null) {
            return;
        }
        
        for (BluetoothGattService service:ble_gatt_.getServices()) {
            BleSensor s = 
                    SensorDb.get(service.getUuid().toString().toLowerCase());
            if (s != null) {
                sensor_list_adapter_.add_sensor(s);
            }
        }
    }
    
    private BluetoothGattCallback gatt_callback_ = 
            new BluetoothGattCallback() {
        
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Message msg = new Message();
            msg.what = UI_EVENT_UPDATE_RSSI;
            msg.arg1 = rssi;
            ui_event_handler_.sendMessage(msg);
        }
        
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, 
                int newState) {
            Log.i(TAG_, "onConnectionStateChange: " + status + " => " + 
                    newState);
            
            if(newState == BluetoothProfile.STATE_CONNECTED){
                status_ = STATUS_CONNECTED;
                
                //
                // Update UI
                //
                Message msg = new Message();
                msg.what = UI_EVENT_UPDATE_DEVICE;
                msg.obj = gatt;
                ui_event_handler_.sendMessage(msg);
                
            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                status_ = STATUS_DISCONNECTED;
                finish();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG_, "onServicesDiscovered, status: " + status);
            
            if(status == BluetoothGatt.GATT_SUCCESS) {
                //
                // Update UI
                //
                Message msg = new Message();
                msg.what = UI_EVENT_UPDATE_SERVICE;
                ui_event_handler_.sendMessage(msg);
                
                //
                // Start runnable for RSSI refreshing
                //
                rssi_refresh_runnable_.run();                
            } else {
            }
        }        
        
        @Override
        public void onCharacteristicRead(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status) {
            Log.i(TAG_, "onCharacteristicRead, status: " + status);
            
            String service_id = characteristic.getService().getUuid().toString();
            String char_id = characteristic.getUuid().toString();
            
            BleSensor sensor = sensor_list_adapter_.get_sensor(service_id);
            if (sensor != null) {
                sensor.onCharacteristicRead(characteristic);
            }
        }
        
        @Override
        public void onCharacteristicChanged(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            Log.i(TAG_, "onCharacteristicChanged");
            
            String service_id = characteristic.getService().getUuid().toString();
            String char_id = characteristic.getUuid().toString();
            
            BleSensor sensor = sensor_list_adapter_.get_sensor(service_id);
            if (sensor != null) {
                sensor.onCharacteristicChanged(characteristic);
                String text = sensor.get_value_string();
                Log.i(TAG_, "value : " + text);
                
                Message msg = new Message();
                msg.what = UI_EVENT_UPDATE_SENSOR_VALUE;
                msg.obj = sensor;
                ui_event_handler_.sendMessage(msg);                
            }
        }
        
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG_, "onCharacteristicWrite: " + status);
        }
        
        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                int status) {
            Log.i(TAG_, "onDescriptorRead: " + status);
        }
        
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                int status) {
            Log.i(TAG_, "onDescriptorWrite: " + status);
        }     
        
        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Log.i(TAG_, "onReliableWriteCompleted: " + status);
        }        
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    public void onReportToggleClicked(View view) {
        boolean on = button_report_server_.isChecked();
        if (on) {
            Log.d(TAG_, "Report button ON");
        } else {
            Log.d(TAG_, "Report button OFF");
        }
    }
    
    public void set_server_errors(int count) {
        String message = "Server Errors: " + count;
        this.textview_server_errors_.setText(message);
    }
    
}
