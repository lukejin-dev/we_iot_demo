package com.ies.mysensortag;

import java.util.ArrayList;
import java.util.List;

import com.ies.blelib.BeaconScanInfo;
import com.ies.blelib.sensor.BleSensor;
import com.ies.blelib.sensor.TiAccelerometerSensor;
import com.ies.blelib.sensor.TiHumiditySensor;
import com.ies.blelib.sensor.TiMagnetometerSensor;
import com.ies.blelib.sensor.TiTemperatureSensor;
import com.ies.mysensortag.DeviceScanListAdapter.ViewHolder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class SensorListAdapter extends BaseAdapter {
    private String TAG = getClass().getSimpleName();
    
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
        
        BleSensor bs = sensor_list_.get(position);
        final String name = bs.get_name();
        Log.v(TAG, "getView: position=" + position + "sensor name:" + name
                + " view=" + view);
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
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.ll_chart);
            layout.addView(createGraphicView(name, view_holder));
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
            view.setTag(view_holder);
        } else {
            view_holder = (ViewHolder) view.getTag();
        }

        if (name != null && name.length() > 0)
            view_holder.name_.setText(name);
        else
            view_holder.name_.setText("unknown sensor");
        
        view_holder.string_value_.setText(bs.get_value_string());
        view_holder.hex_value_.setText(bs.get_raw_value_string());
        updateGraphicView(bs, view_holder);
        return view;
    }
    
    private LineGraphView createGraphicView(String sname, ViewHolder holder) {
        holder.graphic_view_ = new LineGraphView(context_, "real-time data");
        holder.graphic_view_.setViewPort(1, 20);
        holder.graphic_view_.setScalable(true);
        holder.graph_series_1 = new GraphViewSeries(new GraphViewData[] {});
        holder.graph_series_2 = new GraphViewSeries(new GraphViewData[] {});
        holder.graph_series_3 = new GraphViewSeries(new GraphViewData[] {});
        holder.graphic_view_.addSeries(holder.graph_series_1);
        holder.graphic_view_.addSeries(holder.graph_series_2);
        holder.graphic_view_.addSeries(holder.graph_series_3);
        holder.x = 3;
        holder.graph_series_1.getStyle().color = Color.RED;
        holder.graph_series_2.getStyle().color = Color.BLACK;
        holder.graph_series_3.getStyle().color = Color.CYAN;        
        
        if (sname.equalsIgnoreCase("Humidity")) {
            holder.graph_series_1.resetData(new GraphViewData[] {
                    new GraphViewData(1, 30),
                    new GraphViewData(1, 60)
            });
            
            holder.graphic_view_.setDrawBackground(true);
        } else if (sname.equalsIgnoreCase("Accelerator")) {
            holder.graph_series_1.resetData(new GraphViewData[] {
                    new GraphViewData(1, -1),
                    new GraphViewData(2, 1),
            });
            holder.graph_series_2.resetData(new GraphViewData[] {
                    new GraphViewData(1, -1),
                    new GraphViewData(2, 1),
            });
            holder.graph_series_3.resetData(new GraphViewData[] {
                    new GraphViewData(1, -1),
                    new GraphViewData(2, 1),
            });            
        } else if (sname.equalsIgnoreCase("Temperature")) {
            holder.graph_series_1.resetData(new GraphViewData[] {
                    new GraphViewData(1, 5),
                    new GraphViewData(2, 30),
            });
            holder.graph_series_2.resetData(new GraphViewData[] {
                    new GraphViewData(1, 5),
                    new GraphViewData(2, 30),
            });            
        } else if (sname.equalsIgnoreCase("Magnetometer")) {
            holder.graph_series_1.resetData(new GraphViewData[] {
                    new GraphViewData(1, 1),
                    new GraphViewData(2, 140),
            });
            holder.graph_series_2.resetData(new GraphViewData[] {
                    new GraphViewData(1, 1),
                    new GraphViewData(2, 140),
            });
            holder.graph_series_3.resetData(new GraphViewData[] {
                    new GraphViewData(1, 1),
                    new GraphViewData(2, 140),
            });
        }
        return holder.graphic_view_;
    }
    
    private void updateGraphicView(BleSensor sensor, ViewHolder holder) {
        String sname = sensor.get_name();
        
        if (sname.equalsIgnoreCase("Humidity")) {
            Float value = ((TiHumiditySensor)sensor).get_value();
            if (value == null) {
                return;
            }
            holder.graph_series_1.appendData(
                    new GraphViewData(holder.x++, value), true, 20);
        } else if (sname.equalsIgnoreCase("Accelerator")) {
            float[] values = ((TiAccelerometerSensor)sensor).get_value();
            if (values == null) {
                return;
            }
            holder.graph_series_1.appendData(
                    new GraphViewData(holder.x++, values[0]), true, 20);
            holder.graph_series_2.appendData(
                    new GraphViewData(holder.x++, values[1]), true, 20);
            holder.graph_series_3.appendData(
                    new GraphViewData(holder.x++, values[2]), true, 20);
        } else if (sname.equalsIgnoreCase("Temperature")) {
            float[] values = ((TiTemperatureSensor)sensor).get_value();
            if (values == null) {
                return;
            }
            holder.graph_series_1.appendData(
                    new GraphViewData(holder.x++, values[0]), true, 20);
            holder.graph_series_2.appendData(
                    new GraphViewData(holder.x++, values[1]), true, 20);            
        } else if (sname.equalsIgnoreCase("Magnetometer")) {
            float[] values = ((TiMagnetometerSensor)sensor).get_value();
            if (values == null) {
                return;
            }         
            holder.graph_series_1.appendData(
                    new GraphViewData(holder.x++, values[0]), true, 20);
            holder.graph_series_2.appendData(
                    new GraphViewData(holder.x++, values[1]), true, 20);
            holder.graph_series_3.appendData(
                    new GraphViewData(holder.x++, values[2]), true, 20);            
        }        
    }
    private double getRandom() {
        double high = 3;
        double low = 0.5;
        return Math.random() * (high - low) + low;
    }
    
    public void add_sensor(BleSensor sensor) {
        Log.v(TAG, "add sensor:" + sensor.get_name());
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
        LineGraphView graphic_view_;
        GraphViewSeries graph_series_1;
        GraphViewSeries graph_series_2;
        GraphViewSeries graph_series_3;
        int x;
    }    
}
