package com.ies.mysensortag;

import com.example.bluetooth.le.iBeaconClass;
import com.example.bluetooth.le.iBeaconClass.iBeacon;
import com.ies.mysensortag.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
    
    private final static String TAG_ = MainActivity.class.getSimpleName();
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
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        Log.d(TAG_, "onPause");
        
        //
        // Stop the scan on Pause.
        //
    }
    
    
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            final iBeacon ibeacon = iBeaconClass.fromScanData(device,rssi,scanRecord);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(ibeacon);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    
    public void onScanToggleClicked(View view) {
        boolean on = button_scan_switch_.isChecked();
        if (on) {
            Log.d(TAG_, "Scan button ON");
        } else {
            Log.d(TAG_, "Scan button OFF");
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
