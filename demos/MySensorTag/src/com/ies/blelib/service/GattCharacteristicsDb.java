package com.ies.blelib.service;

import java.util.HashMap;

public class GattCharacteristicsDb {

    public static final String UUID_ALERT_CATEGORY = 
            "00002A43-0000-1000-8000-00805f9b34fb";
    public static final String UUID_ALERT_CATEGORY_BIT_MASK = 
            "00002A42-0000-1000-8000-00805f9b34fb";
    public static final String UUID_ALERT_LEVEL = 
            "00002A06-0000-1000-8000-00805f9b34fb";
    public static final String UUID_ALERT_NOTIFICATION_CONTROL_POINT = 
            "00002A44-0000-1000-8000-00805f9b34fb";
    public static final String UUID_ALERT_STATUS = 
            "00002A3F-0000-1000-8000-00805f9b34fb";
    public static final String UUID_APPEARANCE = 
            "00002A01-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BATTERY_LEVEL = 
            "00002A19-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BLOOD_PRESSURE_FEATURE = 
            "00002A49-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BLOOD_PRESSURE_MEASUREMENT = 
            "00002A35-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BODY_SENSOR_LOCATION = 
            "00002A38-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BOOT_KEYBOARD_INPUT_REPORT = 
            "00002A22-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BOOT_KEYBOARD_OUTPUT_REPORT = 
            "00002A32-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BOOT_MOUSE_INPUT_REPORT = 
            "00002A33-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CSC_FEATURE = 
            "00002A5C-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CSC_MEASUREMENT = 
            "00002A5B-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CURRENT_TIME = 
            "00002A2B-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CYCLING_POWER_CONTROL_POINT = 
            "00002A66-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CYCLING_POWER_FEATURE = 
            "00002A65-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CYCLING_POWER_MEASUREMENT = 
            "00002A63-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CYCLING_POWER_VECTOR = 
            "00002A64-0000-1000-8000-00805f9b34fb";
    public static final String UUID_DATE_TIME =
            "00002A08-0000-1000-8000-00805f9b34fb";
    public static final String UUID_DAY_DATE_TIME = 
            "00002A0A-0000-1000-8000-00805f9b34fb";
    public static final String UUID_DAY_OF_WEEK =
            "00002A09-0000-1000-8000-00805f9b34fb";
    public static final String UUID_DEVICE_NAME =
            "00002A00-0000-1000-8000-00805f9b34fb";
    public static final String UUID_DST_OFFSET =
            "00002A0D-0000-1000-8000-00805f9b34fb";
    public static final String UUID_EXACT_TIME_256 =
            "00002A0C-0000-1000-8000-00805f9b34fb";
    public static final String UUID_FIRMWARE_REVISION_STRING = 
            "00002A26-0000-1000-8000-00805f9b34fb";
    public static final String UUID_GLUCOSE_FEATURE = 
            "00002A51-0000-1000-8000-00805f9b34fb";
    public static final String UUID_GLUCOSE_MEASUREMENT =
            "00002A51-0000-1000-8000-00805f9b34fb";
    public static final String UUID_GLUCOSE_MEASUREMENT_CONTEXT =
            "00002A34-0000-1000-8000-00805f9b34fb";
    public static final String UUID_HARDWARE_REVISION_STRING =
            "00002A27-0000-1000-8000-00805f9b34fb";
    public static final String UUID_HEART_RATE_CONTROL_POINT =
            "00002A39-0000-1000-8000-00805f9b34fb";
    public static final String UUID_HEART_RATE_MEASUREMENT =
            "00002A37-0000-1000-8000-00805f9b34fb";
    public static final String UUID_HID_CONTROL_POINT =
            "00002A4C-0000-1000-8000-00805f9b34fb";
    public static final String UUID_HID_INFORMATION =
            "00002A4A-0000-1000-8000-00805f9b34fb";
    public static final String UUID_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST =
            "00002A2A-0000-1000-8000-00805f9b34fb";
    public static final String UUID_INTERMEDIATE_CUFF_PRESSURE =
            "00002A36-0000-1000-8000-00805f9b34fb";
    public static final String UUID_INTERMEDIATE_TEMPERATURE =
            "00002A1E-0000-1000-8000-00805f9b34fb";
    public static final String UUID_LN_CONTROL_POINT =
            "00002A6B-0000-1000-8000-00805f9b34fb";
    public static final String UUID_LN_FEATURE =
            "00002A6A-0000-1000-8000-00805f9b34fb";
    public static final String UUID_LOCAL_TIME_INFROMATION =
            "00002A0F-0000-1000-8000-00805f9b34fb";
    public static final String UUID_LOCATION_AND_SPEED =
            "00002A67-0000-1000-8000-00805f9b34fb";
    public static final String UUID_MANUFACTURER_NAME_STRING = 
            "00002A29-0000-1000-8000-00805f9b34fb";
    public static final String UUID_MEASUREMENT_INTERVAL = 
            "00002A21-0000-1000-8000-00805f9b34fb";
    public static final String UUID_MODEL_NUMBER_STRING =
            "00002A24-0000-1000-8000-00805f9b34fb";
    public static final String UUID_NAVIGATION =
            "00002A68-0000-1000-8000-00805f9b34fb";
    public static final String UUID_NEW_ALERT =
            "00002A46-0000-1000-8000-00805f9b34fb";
    public static final String UUID_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS =
            "00002A04-0000-1000-8000-00805f9b34fb";
    
    
    private static HashMap<String, GattCharacteristics> map = 
            new HashMap<String, GattCharacteristics>();
    
    static {
        map.put(UUID_ALERT_CATEGORY.toLowerCase(), 
                new GattCharacteristics(
                        "Alert Category ID", 
                        "org.bluetooth.characteristic.alert_category_id",
                        UUID_ALERT_CATEGORY));
        map.put(UUID_ALERT_CATEGORY_BIT_MASK.toLowerCase(), 
                new GattCharacteristics(
                        "Alert Category ID bit mask", 
                        "org.bluetooth.characteristic.alert_category_id_bit_mask",
                        UUID_ALERT_CATEGORY_BIT_MASK));
        map.put(UUID_ALERT_LEVEL.toLowerCase(), 
                new GattCharacteristics(
                        "Alert Level", 
                        "org.bluetooth.characteristic.alert_level",
                        UUID_ALERT_LEVEL));        
        map.put(UUID_ALERT_NOTIFICATION_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristics(
                        "Alert Notification Control Point", 
                        "org.bluetooth.characteristic.alert_notification_control_point",
                        UUID_ALERT_NOTIFICATION_CONTROL_POINT));          
        map.put(UUID_ALERT_STATUS.toLowerCase(), 
                new GattCharacteristics(
                        "Alert Status", 
                        "org.bluetooth.characteristic.alert_status",
                        UUID_ALERT_STATUS));         
        map.put(UUID_APPEARANCE.toLowerCase(), 
                new GattCharacteristics(
                        "Appearance", 
                        "org.bluetooth.characteristic.gap.appearance",
                        UUID_APPEARANCE));  
        map.put(UUID_BATTERY_LEVEL.toLowerCase(), 
                new GattCharacteristics(
                        "Battery Level", 
                        "org.bluetooth.characteristic.battery_level",
                        UUID_BATTERY_LEVEL));         
        map.put(UUID_BLOOD_PRESSURE_FEATURE.toLowerCase(), 
                new GattCharacteristics(
                        "Blood Pressure Feature", 
                        "org.bluetooth.characteristic.blood_pressure_feature",
                        UUID_BLOOD_PRESSURE_FEATURE));            
        map.put(UUID_BLOOD_PRESSURE_MEASUREMENT.toLowerCase(), 
                new GattCharacteristics(
                        "Blood Pressure Measurement", 
                        "org.bluetooth.characteristic.blood_pressure_measurement",
                        UUID_BLOOD_PRESSURE_MEASUREMENT));          
        map.put(UUID_BODY_SENSOR_LOCATION.toLowerCase(), 
                new GattCharacteristics(
                        "Body Sensor Location", 
                        "org.bluetooth.characteristic.body_sensor_location",
                        UUID_BODY_SENSOR_LOCATION));         
        map.put(UUID_BOOT_KEYBOARD_INPUT_REPORT.toLowerCase(), 
                new GattCharacteristics(
                        "Boot Keyboard Input Report", 
                        "org.bluetooth.characteristic.boot_keyboard_input_report",
                        UUID_BOOT_KEYBOARD_INPUT_REPORT));         
        map.put(UUID_BOOT_KEYBOARD_OUTPUT_REPORT.toLowerCase(), 
                new GattCharacteristics(
                        "Boot Keyboard Output Report", 
                        "org.bluetooth.characteristic.boot_keyboard_output_report",
                        UUID_BOOT_KEYBOARD_OUTPUT_REPORT));        
        map.put(UUID_BOOT_MOUSE_INPUT_REPORT.toLowerCase(), 
                new GattCharacteristics(
                        "Boot Mouse Input Report", 
                        "org.bluetooth.characteristic.boot_mouse_input_report",
                        UUID_BOOT_MOUSE_INPUT_REPORT)); 
        map.put(UUID_CSC_FEATURE.toLowerCase(), 
                new GattCharacteristics(
                        "CSC Feature", 
                        "org.bluetooth.characteristic.csc_feature",
                        UUID_CSC_FEATURE));         
        map.put(UUID_CSC_MEASUREMENT.toLowerCase(), 
                new GattCharacteristics(
                        "CSC Measurement", 
                        "org.bluetooth.characteristic.csc_measurement",
                        UUID_CSC_MEASUREMENT)); 
        map.put(UUID_CURRENT_TIME.toLowerCase(), 
                new GattCharacteristics(
                        "Current Time", 
                        "org.bluetooth.characteristic.current_time",
                        UUID_CURRENT_TIME));         
        map.put(UUID_CYCLING_POWER_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristics(
                        "Cycling Power Control Point", 
                        "bluetooth.characteristic.cycling_power_control_point",
                        UUID_CYCLING_POWER_CONTROL_POINT)); 
        map.put(UUID_CYCLING_POWER_FEATURE.toLowerCase(), 
                new GattCharacteristics(
                        "Cycling Power Feature", 
                        "org.bluteooth.characteristic.cycling_power_feature",
                        UUID_CYCLING_POWER_FEATURE));         
        map.put(UUID_CYCLING_POWER_MEASUREMENT.toLowerCase(), 
                new GattCharacteristics(
                        "Cycling Power Measurement", 
                        "org.blueeooth.cycling_power_measurement",
                        UUID_CYCLING_POWER_MEASUREMENT)); 
        map.put(UUID_CYCLING_POWER_VECTOR.toLowerCase(), 
                new GattCharacteristics(
                        "Cycling Power Vector", 
                        "org.bluetooth.characteristic.cycling_power_vector",
                        UUID_CYCLING_POWER_VECTOR)); 
        map.put(UUID_DATE_TIME.toLowerCase(), 
                new GattCharacteristics(
                        "Date Time", 
                        "org.bluetooth.characteristic.date_time",
                        UUID_DATE_TIME));         
        map.put(UUID_DAY_DATE_TIME.toLowerCase(), 
                new GattCharacteristics(
                        "Day Date Time", 
                        "org.bluetooth.characteristic.day_date_time",
                        UUID_DAY_DATE_TIME));         
        map.put(UUID_DAY_OF_WEEK.toLowerCase(), 
                new GattCharacteristics(
                        "Day of Week", 
                        "org.bluetooth.characteristic.day_of_week",
                        UUID_DAY_OF_WEEK));         
        map.put(UUID_DEVICE_NAME.toLowerCase(), 
                new GattCharacteristics(
                        "Device Name", 
                        "org.bluetooth.characteristic.gap.device_name",
                        UUID_DEVICE_NAME));         
        map.put(UUID_DST_OFFSET.toLowerCase(), 
                new GattCharacteristics(
                        "DST Offset", 
                        "org.bluetooth.characteristic.dst_offset",
                        UUID_DST_OFFSET));         
        map.put(UUID_EXACT_TIME_256.toLowerCase(), 
                new GattCharacteristics(
                        "Exact Time 256", 
                        "org.bluetooth.characteristic.exact_time_256",
                        UUID_EXACT_TIME_256));          
        map.put(UUID_FIRMWARE_REVISION_STRING.toLowerCase(), 
                new GattCharacteristics(
                        "Firmware Revision String", 
                        "org.bluetooth.characteristic.firmware_revision_string",
                        UUID_FIRMWARE_REVISION_STRING));  
        map.put(UUID_GLUCOSE_FEATURE.toLowerCase(), 
                new GattCharacteristics(
                        "Glucose Feature", 
                        "org.bluetooth.characteristic.glucose_feature",
                        UUID_GLUCOSE_FEATURE));         
        map.put(UUID_GLUCOSE_MEASUREMENT.toLowerCase(), 
                new GattCharacteristics(
                        "Glucose Feature", 
                        "org.bluetooth.characteristic.glucose_feature",
                        UUID_GLUCOSE_FEATURE));
        map.put(UUID_GLUCOSE_MEASUREMENT_CONTEXT.toLowerCase(), 
                new GattCharacteristics(
                        "Glucose Measurement Context", 
                        "org.bluetooth.characteristic.glucose_measurement_context",
                        UUID_GLUCOSE_MEASUREMENT_CONTEXT));        
        map.put(UUID_HARDWARE_REVISION_STRING.toLowerCase(), 
                new GattCharacteristics(
                        "Hardware Revision String", 
                        "org.bluetooth.characteristic.hardware_revision_string",
                        UUID_HARDWARE_REVISION_STRING));         
        map.put(UUID_HEART_RATE_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristics(
                        "Heart Rate Control Point", 
                        "org.bluetooth.characteristic.heart_rate_control_point",
                        UUID_HEART_RATE_CONTROL_POINT));    
        map.put(UUID_HEART_RATE_MEASUREMENT.toLowerCase(), 
                new GattCharacteristics(
                        "Heart Rate Measurement", 
                        "org.bluetooth.characteristic.heart_rate_measurement",
                        UUID_HEART_RATE_MEASUREMENT));        
        map.put(UUID_HID_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristics(
                        "HID Control Point", 
                        "org.bluetooth.characteristic.hid_control_point",
                        UUID_HID_CONTROL_POINT));          
        map.put(UUID_HID_INFORMATION.toLowerCase(), 
                new GattCharacteristics(
                        "HID Information", 
                        "org.bluetooth.characteristic.hid_information",
                        UUID_HID_INFORMATION));         
        map.put(UUID_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST.toLowerCase(), 
                new GattCharacteristics(
                        "IEEE 11073-20601 Regulatory Certification Data List", 
                        "org.bluetooth.characteristic.ieee_11073-20601_regulatory_certification_data_list",
                        UUID_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST));          
        map.put(UUID_INTERMEDIATE_CUFF_PRESSURE.toLowerCase(), 
                new GattCharacteristics(
                        "Intermediate Cuff Pressure", 
                        "org.bluetooth.characteristic.intermediate_blood_pressure",
                        UUID_INTERMEDIATE_CUFF_PRESSURE));         
        map.put(UUID_INTERMEDIATE_TEMPERATURE.toLowerCase(), 
                new GattCharacteristics(
                        "Intermediate Temperature", 
                        "org.bluetooth.characteristic.intermediate_temperature",
                        UUID_INTERMEDIATE_TEMPERATURE));        
        map.put(UUID_LN_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristics(
                        "LN Control Point", 
                        "org.bluetooth.ln_control_point",
                        UUID_LN_CONTROL_POINT));          
        map.put(UUID_LN_FEATURE.toLowerCase(), 
                new GattCharacteristics(
                        "LN Feature", 
                        "org.bluetooth.characteristic.ln_feature",
                        UUID_LN_FEATURE));       
        map.put(UUID_LOCAL_TIME_INFROMATION.toLowerCase(), 
                new GattCharacteristics(
                        "Local Time Information", 
                        "org.bluetooth.characteristic.local_time_information",
                        UUID_LOCAL_TIME_INFROMATION));        
        map.put(UUID_LOCATION_AND_SPEED.toLowerCase(), 
                new GattCharacteristics(
                        "Location and Speed", 
                        "org.bluetooth.location_and_speed",
                        UUID_LOCATION_AND_SPEED));          
        map.put(UUID_MANUFACTURER_NAME_STRING.toLowerCase(), 
                new GattCharacteristics(
                        "Manufacturer Name String", 
                        "org.bluetooth.characteristic.manufacturer_name_string",
                        UUID_MANUFACTURER_NAME_STRING));         
        map.put(UUID_MEASUREMENT_INTERVAL.toLowerCase(), 
                new GattCharacteristics(
                        "Measurement Interval", 
                        "org.bluetooth.characteristic.measurement_interval",
                        UUID_MEASUREMENT_INTERVAL));         
        map.put(UUID_MODEL_NUMBER_STRING.toLowerCase(), 
                new GattCharacteristics(
                        "Model Number String", 
                        "org.bluetooth.characteristic.model_number_string",
                        UUID_MODEL_NUMBER_STRING)); 
        map.put(UUID_NAVIGATION.toLowerCase(), 
                new GattCharacteristics(
                        "Navigation", 
                        "org.bluetooth.characteristic.navigation",
                        UUID_NAVIGATION));        
        map.put(UUID_NEW_ALERT.toLowerCase(), 
                new GattCharacteristics(
                        "New Alert", 
                        "org.bluetooth.characteristic.new_alert",
                        UUID_NEW_ALERT));           
        map.put(UUID_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS.toLowerCase(), 
                new GattCharacteristics(
                        "Peripheral Preferred Connection Parameters", 
                        "org.bluetooth.characteristic.gap.peripheral_preferred_connection_parameters",
                        UUID_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS));          
    }
    
    public static GattCharacteristics get(String uuid) {
        return map.get(uuid.toLowerCase());
    }
}
