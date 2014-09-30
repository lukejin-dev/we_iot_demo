package com.ies.mysensortag;

import java.util.List;

import com.ies.blelib.BeaconScanInfo;
import com.ies.blelib.service.GattService;
import com.ies.blelib.service.GattServiceDb;
import com.ies.mysensortag.DeviceScanListAdapter.ViewHolder;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServiceListAdapter extends BaseAdapter {

    private Activity context_;
    private List<BluetoothGattService> service_list_;
    private LayoutInflater inflator_;
    
    public ServiceListAdapter(Activity c) {
        context_ = c;
        service_list_ = null;
        inflator_ = context_.getLayoutInflater();
    }
    
    @Override
    public int getCount() {
        if (service_list_ == null) {
            return 0;
        }
        return service_list_.size();
    }

    @Override
    public Object getItem(int position) {
        if (service_list_ == null) {
            return null;
        }
        assert (position < service_list_.size());
        return service_list_.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (service_list_ == null) {
            return null;
        }
        
        ViewHolder view_holder;
        
        // General ListView optimization code.
        if (view == null) {
            view = inflator_.inflate(R.layout.device_service, null);
            view_holder = new ViewHolder();
            view_holder.service_name_ = 
                    (TextView) view.findViewById(R.id.textview_service_name);
            view_holder.service_uuid_ = 
                    (TextView) view.findViewById(R.id.textview_service_uuid);
            view_holder.service_type_ =
                    (TextView)view.findViewById(R.id.textview_service_type);
            view.setTag(view_holder);
        } else {
            view_holder = (ViewHolder) view.getTag();
        }

        BluetoothGattService service = service_list_.get(position);
        GattService gs = GattServiceDb.get(service.getUuid().toString());
        if (gs != null) {
            view_holder.service_name_.setText(gs.get_name());
            view_holder.service_uuid_.setText(service.getUuid().toString());
            view_holder.service_type_.setText(gs.get_type());
        } else {
            view_holder.service_name_.setText("Unknown Service");
            view_holder.service_uuid_.setText(service.getUuid().toString());
            view_holder.service_type_.setText("" + service.getType());
            
        }
        return view;
    }
    
    public void set_list(List<BluetoothGattService> service_list) {
        service_list_ = service_list;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView service_name_;
        TextView service_uuid_;
        TextView service_type_;
    }    
}
