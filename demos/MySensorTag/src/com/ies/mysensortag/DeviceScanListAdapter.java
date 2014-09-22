package com.ies.mysensortag;

import java.util.ArrayList;

import com.ies.blelib.BeaconScanInfo;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DeviceScanListAdapter extends BaseAdapter {
    
    private ArrayList<BeaconScanInfo> beacon_list_;
    private LayoutInflater inflator_;
    private Activity context_;
    
    public DeviceScanListAdapter(Activity context) {
        context_ = context;
        beacon_list_ = new ArrayList<BeaconScanInfo>();
        inflator_ = context_.getLayoutInflater();
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return beacon_list_.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return beacon_list_.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder view_holder;
        
        // General ListView optimization code.
        if (view == null) {
            view = inflator_.inflate(R.layout.device_scan_element, null);
            view_holder = new ViewHolder();
            view_holder.device_address_ = 
                    (TextView) view.findViewById(R.id.text_mac);
            view_holder.device_name_ = 
                    (TextView) view.findViewById(R.id.text_name);
            view_holder.device_uuid_ = 
                    (TextView)view.findViewById(R.id.text_uuid);
            view_holder.device_rssi_ =
                    (TextView)view.findViewById(R.id.text_rssi);
            view.setTag(view_holder);
        } else {
            view_holder = (ViewHolder) view.getTag();
        }

        BeaconScanInfo bsi = beacon_list_.get(position);
        final String name = bsi.get_name();
        if (name != null && name.length() > 0)
            view_holder.device_name_.setText(name);
        else
            view_holder.device_name_.setText(R.string.unknown_scan_device);
        
        view_holder.device_address_.setText(bsi.get_address());
        //view_holder.device_uuid_.setText(bsi.get);
        view_holder.device_rssi_.setText("" + bsi.get_rssi());

        return view;
    }

    public void clear() {
        beacon_list_.clear();
        notifyDataSetChanged();
    }
    
    public void update_device (
            BluetoothDevice device, int rssi, byte[] scanRecord) {
        //
        // Check if exist
        //
        BeaconScanInfo bsi = get_scan_object(device.getAddress());
        if (bsi == null) {
            //
            // Create a new one
            //
            bsi = new BeaconScanInfo(
                    device.getName(), 
                    device.getAddress(),
                    rssi);
            beacon_list_.add(bsi);
        }
        
        //
        // Only update the RSSI value for existing beacon device.
        //
        bsi.set_rssi(rssi);
        notifyDataSetChanged();
    }
    
    private BeaconScanInfo get_scan_object(String address) {
        for (BeaconScanInfo item:beacon_list_) {
            if (item.get_address().compareToIgnoreCase(address) == 0) {
                return item;
            }
        }
        return null;
    }
    
    class ViewHolder {
        TextView device_name_;
        TextView device_address_;
        TextView device_uuid_;
        TextView device_rssi_;
    }    
}
