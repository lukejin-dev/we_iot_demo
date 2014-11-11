package com.ies.mysensortag;

import com.ies.blelib.BleScaner;
import com.ies.mysensortag.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DeviceScanActivity extends Activity {
    
    private final static String TAG_ = DeviceScanActivity.class.getSimpleName();
    
    private final long DISAPPEAR_CHECK_INTERVAL_ = 1000;
    private ToggleButton button_scan_switch_;
    private BluetoothAdapter ble_adapter_;
    private ListView listview_scan_;
    private DeviceScanListAdapter list_adapter_scan_;
    private Handler disappear_check_handler_;
    private BleScaner ble_scaner_;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //
        // Prepare UI layout and UI controls.
        //
        setContentView(R.layout.device_scan_main);
        button_scan_switch_ = 
                (ToggleButton) findViewById(R.id.scan_swtich_button);
        listview_scan_ = (ListView) findViewById(R.id.device_list);
        list_adapter_scan_ = new DeviceScanListAdapter(this);
        listview_scan_.setAdapter(list_adapter_scan_);
        listview_scan_.setOnItemClickListener(
                list_adapter_scan_.get_item_click_listener());
        
        //
        // Use this check to determine whether BLE is supported on the device.  
        // Then you can selectively disable BLE-related features.
        //
        if (!getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, 
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        
        //
        // Initializes a Bluetooth adapter.  For API level 18 and above, get 
        // a reference to BluetoothAdapter through BluetoothManager.
        //
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ble_adapter_ = bluetoothManager.getAdapter();    
        
        //
        // Checks if Bluetooth is supported on the device.
        //
        if (ble_adapter_ == null) {
            Toast.makeText(this, R.string.ble_not_supported, 
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        //
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        //
        if (ble_adapter_ == null || !ble_adapter_.isEnabled()) {
            Intent enableBtIntent = 
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }
        
        ble_scaner_ = new BleScaner();
        disappear_check_handler_ = new Handler();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG_, "onResume");
        
        //
        // Start the scan on Resume. 
        //
        if (button_scan_switch_.isChecked()) {
            ble_scaner_.startScan(scan_callback_, ble_adapter_);
            disappear_check_handler_.postDelayed(
                    disappear_check_runner_, DISAPPEAR_CHECK_INTERVAL_);
        } 
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        Log.d(TAG_, "onPause");
        
        //
        // Stop the scan on Pause.
        //
        if (button_scan_switch_.isChecked()) {
            ble_scaner_.stopLeScan(scan_callback_, ble_adapter_);
            list_adapter_scan_.clear();
        }
    }
   
    final Runnable disappear_check_runner_ = new Runnable() {
        public void run() {
            if (button_scan_switch_.isChecked()) {
                list_adapter_scan_.refresh_disappeared_device();
                disappear_check_handler_.postDelayed(this, 
                        DISAPPEAR_CHECK_INTERVAL_);
            }
        }
    };
    
    public void onScanToggleClicked(View view) {
        boolean on = button_scan_switch_.isChecked();
        if (on) {
            Log.d(TAG_, "Scan button ON");
            
            //
            // Ensures Bluetooth is available on the device and it is enabled. 
            // If not, displays a dialog requesting user permission to enable 
            // Bluetooth.
            //
            if (ble_adapter_ == null || !ble_adapter_.isEnabled()) {
                Intent enableBtIntent = 
                        new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }
            
            if (ble_adapter_.isEnabled()) {
                ble_scaner_.startScan(scan_callback_, ble_adapter_);
                disappear_check_handler_.postDelayed(
                        disappear_check_runner_, DISAPPEAR_CHECK_INTERVAL_);
            } else {
                button_scan_switch_.setChecked(false);
            }
        } else {
            Log.d(TAG_, "Scan button OFF");
            ble_scaner_.stopLeScan(scan_callback_, ble_adapter_);
            list_adapter_scan_.clear();
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback scan_callback_ =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, 
                final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list_adapter_scan_.update_device(device, rssi, scanRecord);
                }
            });
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
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(this, SettingPreferenceActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
