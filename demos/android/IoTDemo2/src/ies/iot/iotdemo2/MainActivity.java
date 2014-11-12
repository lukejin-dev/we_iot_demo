package ies.iot.iotdemo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ies.iot.demolib.utils.*;
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String name = DemoSettings.getInstance().getDeviceName(this);
        String address = DemoSettings.getInstance().getDeviceAddress(this);
        
        if (name == null || address == null) {
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
