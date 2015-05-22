package com.ies.mysensortag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ies.blelib.BeaconScanInfo;
import com.ies.blelib.service.GattCharacteristicsInfo;
import com.ies.blelib.service.GattCharacteristicsDb;
import com.ies.blelib.service.GattServiceInfo;
import com.ies.blelib.service.GattServiceDb;
import com.ies.mysensortag.DeviceScanListAdapter.ViewHolder;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ServiceListAdapter extends BaseExpandableListAdapter {

    private final static String TAG_ = 
            ServiceListAdapter.class.getSimpleName();
    
    private Activity context_;
    private List<BluetoothGattService> service_list_;
    private LayoutInflater inflator_;
    
    public ServiceListAdapter(Activity c) {
        context_ = c;
        service_list_ = null;
        inflator_ = context_.getLayoutInflater();
    }
    
    public Object getChild(int group_pos, int child_pos) {
        return  service_list_.get(group_pos).getCharacteristics().get(child_pos);
    }
    
    public  long  getChildId(int  groupPosition, int  childPosition) {
        return  childPosition;
    }
    
    public View getChildView(int groupPosition, int childPosition,
            boolean  isLastChild, View view, ViewGroup parent) {
        if (service_list_ == null) {
            return null;
        }
        
        CharViewHolder view_holder;
        
        // General ListView optimization code.
        if (view == null) {
            view = inflator_.inflate(R.layout.gatt_characteristics, null);
            view_holder = new CharViewHolder();
            view_holder.char_name_ = 
                    (TextView) view.findViewById(R.id.char_name);
            view_holder.char_uuid_ = 
                    (TextView) view.findViewById(R.id.char_uuid);
            view.setTag(view_holder);
        } else {
            view_holder = (CharViewHolder) view.getTag();
        }

        BluetoothGattCharacteristic ble_gatt_char = 
                service_list_.get(groupPosition).getCharacteristics().get(childPosition);
        GattCharacteristicsInfo gatt_char = 
                GattCharacteristicsDb.get(ble_gatt_char.getUuid().toString());
        if (gatt_char != null) {
            view_holder.char_name_.setText(gatt_char.get_name());
            view_holder.char_uuid_.setText(gatt_char.get_uuid());
        } else {
            view_holder.char_name_.setText("Unknown Service");
            view_holder.char_uuid_.setText(ble_gatt_char.getUuid().toString());
        }
        return view;        
    }
    
    public  Object getGroup(int  groupPosition)  {  
        return  service_list_.get(groupPosition);  
    }      
    
    public  int  getGroupCount() {  
        if (service_list_ == null) {
            return 0;
        }        
        return  service_list_.size();  
    }      
    
    public  long getGroupId(int  groupPosition) {  
        return  groupPosition;  
    }      
    
    public  View getGroupView(int  groupPosition, boolean  isExpanded,  
            View view, ViewGroup parent) {  
        if (service_list_ == null) {
            return null;
        }
        
        ServiceViewHolder view_holder;
        
        // General ListView optimization code.
        if (view == null) {
            view = inflator_.inflate(R.layout.device_service, null);
            view_holder = new ServiceViewHolder();
            view_holder.service_name_ = 
                    (TextView) view.findViewById(R.id.textview_service_name);
            view_holder.service_uuid_ = 
                    (TextView) view.findViewById(R.id.textview_service_uuid);
            view.setTag(view_holder);
        } else {
            view_holder = (ServiceViewHolder) view.getTag();
        }

        BluetoothGattService service = service_list_.get(groupPosition);
        GattServiceInfo gs = GattServiceDb.get(service.getUuid().toString());
        if (gs != null) {
            view_holder.service_name_.setText(gs.get_name());
            view_holder.service_uuid_.setText(service.getUuid().toString());
        } else {
            view_holder.service_name_.setText("Unknown Service");
            view_holder.service_uuid_.setText(service.getUuid().toString());
        }
        return view; 
    }      
    
    public  boolean  hasStableIds() {   
        return  false ;  
    }      
    
    public boolean isChildSelectable(int groupPosition, int childPosition) {  
        return  true ;  
    }      
    
    public void set_list(List<BluetoothGattService> service_list) {
        service_list_ = service_list;
        this.notifyDataSetChanged();
    }

    class ServiceViewHolder {
        TextView service_name_;
        TextView service_uuid_;
    }
    
    class CharViewHolder {
        TextView char_name_;
        TextView char_uuid_;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (service_list_ == null) {
            return 0;
        }           
        return service_list_.get(groupPosition).getCharacteristics().size();
    }
}
