package com.ies.mysensortag;

import java.util.ArrayList;
import java.util.List;

import com.ies.blelib.BeaconScanInfo;
import com.ies.blelib.sensor.BleSensor;
import com.ies.mysensortag.DeviceScanListAdapter.ViewHolder;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class SensorListAdapter extends BaseAdapter {

    private List<BleSensor> sensor_list_;
    private Activity context_;
    private LayoutInflater inflator_;
    
    public SensorListAdapter(Activity context) {
        sensor_list_ = new ArrayList<BleSensor>();
        context_ = context;
        inflator_ = context_.getLayoutInflater();
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return sensor_list_.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return sensor_list_.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder view_holder;
        
        // General ListView optimization code.
        if (view == null) {
            view = inflator_.inflate(R.layout.sensor_item, null);
            view_holder = new ViewHolder();
            view_holder.name_ = 
                    (TextView) view.findViewById(R.id.textview_sensor_name);
            view_holder.string_value_ = 
                    (TextView) view.findViewById(R.id.textview_sensor_string_value);
            view_holder.switch_ = 
                    (Switch) view.findViewById(R.id.switch_enable);
            view_holder.hex_value_ =
                    (TextView)view.findViewById(R.id.textview_sensor_hex_value);
            view.setTag(view_holder);
            view_holder.switch_.setOnCheckedChangeListener(
                    new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                boolean isChecked) {
                            // TODO Auto-generated method stub
                            BleSensor bs = sensor_list_.get(position);
                            BluetoothGatt gatt = ((DeviceActivity)context_).get_gatt();
                            if (gatt != null)
                                bs.enable(gatt, isChecked);
                        }
                        
                    });
        } else {
            view_holder = (ViewHolder) view.getTag();
        }

        BleSensor bs = sensor_list_.get(position);
        final String name = bs.get_name();
        if (name != null && name.length() > 0)
            view_holder.name_.setText(name);
        else
            view_holder.name_.setText("unknown sensor");
        
        view_holder.string_value_.setText(bs.get_value_string());
        view_holder.hex_value_.setText(bs.get_raw_value_string());
        
        return view;
    }

    public void add_sensor(BleSensor sensor) {
        sensor_list_.add(sensor);
        notifyDataSetChanged();
    }
    
    public BleSensor get_sensor(String service_id) {
        for (BleSensor s:sensor_list_) {
            if (s.get_service_uuid().equalsIgnoreCase(service_id)) {
                return s;
            }
        }
        return null;
    }
    
    public void update_sensor_value(BleSensor s) {
        
    }
    class ViewHolder {
        TextView name_;
        Switch switch_;
        TextView string_value_;
        TextView hex_value_;
    }    
}
