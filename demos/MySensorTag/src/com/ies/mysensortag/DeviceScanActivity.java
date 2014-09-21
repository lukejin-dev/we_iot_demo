package com.ies.mysensortag;

import java.lang.reflect.Method;

import com.ies.mysensortag.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DeviceScanActivity extends Activity {
    
    private final static String TAG_ = DeviceScanActivity.class.getSimpleName();
    private ToggleButton button_scan_switch_;
    private ToggleButton button_report_server_;
    private BluetoothAdapter ble_adapter_;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_scan_switch_ = (ToggleButton) findViewById(R.id.scan_swtich_button);
        button_report_server_ = (ToggleButton) findViewById(R.id.report_server_button);
        
        //
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        //
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        
        //
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        //
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        ble_adapter_ = bluetoothManager.getAdapter();    
        
        //
        // Checks if Bluetooth is supported on the device.
        //
        if (ble_adapter_ == null) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        //
        // Enable bluetooth
        //
        ble_adapter_.enable();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG_, "onResume");
        
        //
        // Start the scan on Resume. 
        //
        if (button_scan_switch_.isChecked()) {
            ble_adapter_.startLeScan(scan_callback_);
        } 
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        Log.d(TAG_, "onPause");
        
        //
        // Stop the scan on Pause.
        //
        ble_adapter_.stopLeScan(scan_callback_);
    }
   
    public void onScanToggleClicked(View view) {
        boolean on = button_scan_switch_.isChecked();
        if (on) {
            Log.d(TAG_, "Scan button ON");
            ble_adapter_.startLeScan(scan_callback_);
        } else {
            Log.d(TAG_, "Scan button OFF");
            ble_adapter_.stopLeScan(scan_callback_);
        }
    }

    public void onReportToggleClicked(View view) {
        boolean on = button_report_server_.isChecked();
        if (on) {
            Log.d(TAG_, "Report button ON");
        } else {
            Log.d(TAG_, "Report button OFF");
        }
    }
    
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback scan_callback_ =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            //BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            try {
                Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
                ParcelUuid[] ids = (ParcelUuid[]) getUuidsMethod.invoke(ble_adapter_, null);
                for (ParcelUuid id:ids) {
                    Log.d(TAG_, "id:" + id.toString());
                }
            } catch (Exception e) {
                
            }
            Log.d(TAG_, "Get device " + scanRecord + ", RSSI:" + rssi);
            /**
            final iBeacon ibeacon = iBeaconClass.fromScanData(device,rssi,scanRecord);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(ibeacon);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
            **/
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
