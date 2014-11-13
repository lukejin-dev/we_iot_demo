package ies.iot.iotdemo2;

import ies.iot.demolib.sensors.BleSensor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class SensorListAdapter extends BaseAdapter {
   
    private List<BleSensor> sensor_list_;
    private Activity context_;
    private LayoutInflater inflator_;
    
    public SensorListAdapter(Activity context) {
        context_ = context;
        inflator_ = context_.getLayoutInflater();
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (sensor_list_ == null) {
            return 0;
        }
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
                    (TextView) view.findViewById(R.id.tv_sensor_name);
            view_holder.string_value_ = 
                    (TextView) view.findViewById(R.id.tv_sensor_string_value);
            view_holder.hex_value_ =
                    (TextView)view.findViewById(R.id.tv_sensor_hex_value);
            view.setTag(view_holder);
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

    public void add_sensors(List<BleSensor> sensorList) {
        sensor_list_ = sensorList;
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
    
    class ViewHolder {
        TextView name_;
        Switch switch_;
        TextView string_value_;
        TextView hex_value_;
    }

}
