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
    public static final String UUID_PERIPHERAL_PRIVACY_FLAG =
            "00002A02-0000-1000-8000-00805f9b34fb";
    public static final String UUID_PNP_ID = 
            "00002A50-0000-1000-8000-00805f9b34fb";
    public static final String UUID_POSITION_QUALITY = 
            "00002A69-0000-1000-8000-00805f9b34fb";
    public static final String UUID_PROTOCOL_MODE = 
            "00002A4E-0000-1000-8000-00805f9b34fb";
    public static final String UUID_RECONNECTION_ADDRESS =
            "00002A03-0000-1000-8000-00805f9b34fb";
    public static final String UUID_RECORD_ACCESS_CONTROL_POINT =
            "00002A52-0000-1000-8000-00805f9b34fb";
    public static final String UUID_REFERENCE_TIME_INFORMATION =
            "00002A14-0000-1000-8000-00805f9b34fb";
    public static final String UUID_REPORT =
            "00002A4D-0000-1000-8000-00805f9b34fb";
    public static final String UUID_REPORT_MAP =
            "00002A4B-0000-1000-8000-00805f9b34fb";
    public static final String UUID_RINGER_CONTROL_POINT = 
            "00002A40-0000-1000-8000-00805f9b34fb";
    public static final String UUID_RINGER_SETTING = 
            "00002A41-0000-1000-8000-00805f9b34fb";
    public static final String UUID_RSC_FEATURE = 
            "00002A54-0000-1000-8000-00805f9b34fb";
    public static final String UUID_RSC_MEASUREMENT =
            "00002A53-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SC_CONTROL_POINT =
            "00002A55-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SCAN_INTERVAL_WINDOW =
            "00002A4F-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SCAN_REFRESH =
            "00002A31-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SENSOR_LOCATION =
            "00002A5D-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SERIAL_NUMBER_STRING =
            "00002A25-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SERVICE_CHANGED =
            "00002A05-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SOFTWARE_REVISION_STRING = 
            "00002A28-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SUPPORTED_NEW_ALERT_CATEGORY =
            "00002A47-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SUPPORTED_UNREAD_ALERT_CATEGORY =
            "00002A48-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SYSTEM_ID = 
            "00002A23-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TEMPERATURE_MEASUREMENT =
            "00002A1C-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TEMPERATURE_TYPE =
            "00002A1D-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TIME_ACCURACY =
            "00002A12-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TIME_SOURCE =
            "00002A13-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TIME_UPDATE_CONTROL_POINT =
            "00002A16-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TIME_UPDATE_STATE =
            "00002A17-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TIME_WITH_DST =
            "00002A11-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TIME_ZONE = 
            "00002A0E-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TX_POWER_LEVEL =
            "00002A07-0000-1000-8000-00805f9b34fb";
    public static final String UUID_UNREAD_ALERT_STATUS =
            "00002A45-0000-1000-8000-00805f9b34fb";
    public static final String UUID_ADVERTISING_INTERVAL =
            "00002b30-0000-1000-8000-00805f9b34fb";
    public static final String UUID_ACCELERATION_ORIENTATION =
            "00002b20-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TEMPERATURE =
            "00002b10-0000-1000-8000-00805f9b34fb";
    
    
    private static HashMap<String, GattCharacteristicsInfo> map = 
            new HashMap<String, GattCharacteristicsInfo>();
    
    static {
        map.put(UUID_ALERT_CATEGORY.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Alert Category ID", 
                        "org.bluetooth.characteristic.alert_category_id",
                        UUID_ALERT_CATEGORY));
        map.put(UUID_ALERT_CATEGORY_BIT_MASK.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Alert Category ID bit mask", 
                        "org.bluetooth.characteristic.alert_category_id_bit_mask",
                        UUID_ALERT_CATEGORY_BIT_MASK));
        map.put(UUID_ALERT_LEVEL.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Alert Level", 
                        "org.bluetooth.characteristic.alert_level",
                        UUID_ALERT_LEVEL));        
        map.put(UUID_ALERT_NOTIFICATION_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Alert Notification Control Point", 
                        "org.bluetooth.characteristic.alert_notification_control_point",
                        UUID_ALERT_NOTIFICATION_CONTROL_POINT));          
        map.put(UUID_ALERT_STATUS.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Alert Status", 
                        "org.bluetooth.characteristic.alert_status",
                        UUID_ALERT_STATUS));         
        map.put(UUID_APPEARANCE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Appearance", 
                        "org.bluetooth.characteristic.gap.appearance",
                        UUID_APPEARANCE));  
        map.put(UUID_BATTERY_LEVEL.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Battery Level", 
                        "org.bluetooth.characteristic.battery_level",
                        UUID_BATTERY_LEVEL));         
        map.put(UUID_BLOOD_PRESSURE_FEATURE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Blood Pressure Feature", 
                        "org.bluetooth.characteristic.blood_pressure_feature",
                        UUID_BLOOD_PRESSURE_FEATURE));            
        map.put(UUID_BLOOD_PRESSURE_MEASUREMENT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Blood Pressure Measurement", 
                        "org.bluetooth.characteristic.blood_pressure_measurement",
                        UUID_BLOOD_PRESSURE_MEASUREMENT));          
        map.put(UUID_BODY_SENSOR_LOCATION.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Body Sensor Location", 
                        "org.bluetooth.characteristic.body_sensor_location",
                        UUID_BODY_SENSOR_LOCATION));         
        map.put(UUID_BOOT_KEYBOARD_INPUT_REPORT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Boot Keyboard Input Report", 
                        "org.bluetooth.characteristic.boot_keyboard_input_report",
                        UUID_BOOT_KEYBOARD_INPUT_REPORT));         
        map.put(UUID_BOOT_KEYBOARD_OUTPUT_REPORT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Boot Keyboard Output Report", 
                        "org.bluetooth.characteristic.boot_keyboard_output_report",
                        UUID_BOOT_KEYBOARD_OUTPUT_REPORT));        
        map.put(UUID_BOOT_MOUSE_INPUT_REPORT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Boot Mouse Input Report", 
                        "org.bluetooth.characteristic.boot_mouse_input_report",
                        UUID_BOOT_MOUSE_INPUT_REPORT)); 
        map.put(UUID_CSC_FEATURE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "CSC Feature", 
                        "org.bluetooth.characteristic.csc_feature",
                        UUID_CSC_FEATURE));         
        map.put(UUID_CSC_MEASUREMENT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "CSC Measurement", 
                        "org.bluetooth.characteristic.csc_measurement",
                        UUID_CSC_MEASUREMENT)); 
        map.put(UUID_CURRENT_TIME.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Current Time", 
                        "org.bluetooth.characteristic.current_time",
                        UUID_CURRENT_TIME));         
        map.put(UUID_CYCLING_POWER_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Cycling Power Control Point", 
                        "bluetooth.characteristic.cycling_power_control_point",
                        UUID_CYCLING_POWER_CONTROL_POINT)); 
        map.put(UUID_CYCLING_POWER_FEATURE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Cycling Power Feature", 
                        "org.bluteooth.characteristic.cycling_power_feature",
                        UUID_CYCLING_POWER_FEATURE));         
        map.put(UUID_CYCLING_POWER_MEASUREMENT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Cycling Power Measurement", 
                        "org.blueeooth.cycling_power_measurement",
                        UUID_CYCLING_POWER_MEASUREMENT)); 
        map.put(UUID_CYCLING_POWER_VECTOR.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Cycling Power Vector", 
                        "org.bluetooth.characteristic.cycling_power_vector",
                        UUID_CYCLING_POWER_VECTOR)); 
        map.put(UUID_DATE_TIME.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Date Time", 
                        "org.bluetooth.characteristic.date_time",
                        UUID_DATE_TIME));         
        map.put(UUID_DAY_DATE_TIME.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Day Date Time", 
                        "org.bluetooth.characteristic.day_date_time",
                        UUID_DAY_DATE_TIME));         
        map.put(UUID_DAY_OF_WEEK.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Day of Week", 
                        "org.bluetooth.characteristic.day_of_week",
                        UUID_DAY_OF_WEEK));         
        map.put(UUID_DEVICE_NAME.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Device Name", 
                        "org.bluetooth.characteristic.gap.device_name",
                        UUID_DEVICE_NAME));         
        map.put(UUID_DST_OFFSET.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "DST Offset", 
                        "org.bluetooth.characteristic.dst_offset",
                        UUID_DST_OFFSET));         
        map.put(UUID_EXACT_TIME_256.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Exact Time 256", 
                        "org.bluetooth.characteristic.exact_time_256",
                        UUID_EXACT_TIME_256));          
        map.put(UUID_FIRMWARE_REVISION_STRING.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Firmware Revision String", 
                        "org.bluetooth.characteristic.firmware_revision_string",
                        UUID_FIRMWARE_REVISION_STRING));  
        map.put(UUID_GLUCOSE_FEATURE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Glucose Feature", 
                        "org.bluetooth.characteristic.glucose_feature",
                        UUID_GLUCOSE_FEATURE));         
        map.put(UUID_GLUCOSE_MEASUREMENT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Glucose Feature", 
                        "org.bluetooth.characteristic.glucose_feature",
                        UUID_GLUCOSE_FEATURE));
        map.put(UUID_GLUCOSE_MEASUREMENT_CONTEXT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Glucose Measurement Context", 
                        "org.bluetooth.characteristic.glucose_measurement_context",
                        UUID_GLUCOSE_MEASUREMENT_CONTEXT));        
        map.put(UUID_HARDWARE_REVISION_STRING.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Hardware Revision String", 
                        "org.bluetooth.characteristic.hardware_revision_string",
                        UUID_HARDWARE_REVISION_STRING));         
        map.put(UUID_HEART_RATE_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Heart Rate Control Point", 
                        "org.bluetooth.characteristic.heart_rate_control_point",
                        UUID_HEART_RATE_CONTROL_POINT));    
        map.put(UUID_HEART_RATE_MEASUREMENT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Heart Rate Measurement", 
                        "org.bluetooth.characteristic.heart_rate_measurement",
                        UUID_HEART_RATE_MEASUREMENT));        
        map.put(UUID_HID_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "HID Control Point", 
                        "org.bluetooth.characteristic.hid_control_point",
                        UUID_HID_CONTROL_POINT));          
        map.put(UUID_HID_INFORMATION.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "HID Information", 
                        "org.bluetooth.characteristic.hid_information",
                        UUID_HID_INFORMATION));         
        map.put(UUID_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "IEEE 11073-20601 Regulatory Certification Data List", 
                        "org.bluetooth.characteristic.ieee_11073-20601_regulatory_certification_data_list",
                        UUID_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST));          
        map.put(UUID_INTERMEDIATE_CUFF_PRESSURE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Intermediate Cuff Pressure", 
                        "org.bluetooth.characteristic.intermediate_blood_pressure",
                        UUID_INTERMEDIATE_CUFF_PRESSURE));         
        map.put(UUID_INTERMEDIATE_TEMPERATURE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Intermediate Temperature", 
                        "org.bluetooth.characteristic.intermediate_temperature",
                        UUID_INTERMEDIATE_TEMPERATURE));        
        map.put(UUID_LN_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "LN Control Point", 
                        "org.bluetooth.ln_control_point",
                        UUID_LN_CONTROL_POINT));          
        map.put(UUID_LN_FEATURE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "LN Feature", 
                        "org.bluetooth.characteristic.ln_feature",
                        UUID_LN_FEATURE));       
        map.put(UUID_LOCAL_TIME_INFROMATION.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Local Time Information", 
                        "org.bluetooth.characteristic.local_time_information",
                        UUID_LOCAL_TIME_INFROMATION));        
        map.put(UUID_LOCATION_AND_SPEED.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Location and Speed", 
                        "org.bluetooth.location_and_speed",
                        UUID_LOCATION_AND_SPEED));          
        map.put(UUID_MANUFACTURER_NAME_STRING.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Manufacturer Name String", 
                        "org.bluetooth.characteristic.manufacturer_name_string",
                        UUID_MANUFACTURER_NAME_STRING));         
        map.put(UUID_MEASUREMENT_INTERVAL.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Measurement Interval", 
                        "org.bluetooth.characteristic.measurement_interval",
                        UUID_MEASUREMENT_INTERVAL));         
        map.put(UUID_MODEL_NUMBER_STRING.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Model Number String", 
                        "org.bluetooth.characteristic.model_number_string",
                        UUID_MODEL_NUMBER_STRING)); 
        map.put(UUID_NAVIGATION.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Navigation", 
                        "org.bluetooth.characteristic.navigation",
                        UUID_NAVIGATION));        
        map.put(UUID_NEW_ALERT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "New Alert", 
                        "org.bluetooth.characteristic.new_alert",
                        UUID_NEW_ALERT));           
        map.put(UUID_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Peripheral Preferred Connection Parameters", 
                        "org.bluetooth.characteristic.gap.peripheral_preferred_connection_parameters",
                        UUID_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS));    
        map.put(UUID_PERIPHERAL_PRIVACY_FLAG.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Peripheral Privacy Flag", 
                        "org.bluetooth.characteristic.gap.peripheral_privacy_flag",
                        UUID_PERIPHERAL_PRIVACY_FLAG));          
        map.put(UUID_PNP_ID.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "PnP ID", 
                        "org.bluetooth.characteristic.pnp_id",
                        UUID_PNP_ID));          
        map.put(UUID_POSITION_QUALITY.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Position Quality", 
                        "org.bluetooth.position_quality",
                        UUID_POSITION_QUALITY));         
        map.put(UUID_PROTOCOL_MODE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Protocol Mode", 
                        "org.bluetooth.characteristic.protocol_mode",
                        UUID_PROTOCOL_MODE));        
        map.put(UUID_RECONNECTION_ADDRESS.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Reconnection Address", 
                        "org.bluetooth.characteristic.gap.reconnection_address",
                        UUID_RECONNECTION_ADDRESS));          
        map.put(UUID_RECORD_ACCESS_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Record Access Control Point", 
                        "org.bluetooth.characteristic.record_access_control_point",
                        UUID_RECORD_ACCESS_CONTROL_POINT));         
        map.put(UUID_REFERENCE_TIME_INFORMATION.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Reference Time Information", 
                        "org.bluetooth.characteristic.reference_time_information",
                        UUID_REFERENCE_TIME_INFORMATION));         
        map.put(UUID_REPORT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Report", 
                        "org.bluetooth.characteristic.report",
                        UUID_REPORT));        
        map.put(UUID_REPORT_MAP.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Report Map", 
                        "org.bluetooth.characteristic.report_map",
                        UUID_REPORT_MAP));         
        map.put(UUID_RINGER_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Ringer Control Point", 
                        "org.bluetooth.characteristic.ringer_control_point",
                        UUID_RINGER_CONTROL_POINT));          
        map.put(UUID_RINGER_SETTING.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Ringer Setting", 
                        "org.bluetooth.characteristic.ringer_setting",
                        UUID_RINGER_SETTING));         
        map.put(UUID_RSC_FEATURE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "RSC Feature", 
                        "org.bluetooth.characteristic.rsc_feature",
                        UUID_RSC_FEATURE));          
        map.put(UUID_RSC_MEASUREMENT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "RSC Measurement", 
                        "org.bluetooth.characteristic.rsc_measurement",
                        UUID_RSC_MEASUREMENT));         
        map.put(UUID_SC_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "SC Control Point", 
                        "org.bluetooth.characteristic.sc_control_point",
                        UUID_SC_CONTROL_POINT));        
        map.put(UUID_SCAN_INTERVAL_WINDOW.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Scan Interval Window", 
                        "org.bluetooth.characteristic.scan_interval_window",
                        UUID_SCAN_INTERVAL_WINDOW));         
        map.put(UUID_SCAN_REFRESH.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Scan Refresh", 
                        "org.bluetooth.characteristic.scan_refresh",
                        UUID_SCAN_REFRESH));          
        map.put(UUID_SENSOR_LOCATION.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Sensor Location", 
                        "org.bluetooth.characteristic.sensor_location",
                        UUID_SENSOR_LOCATION));         
        map.put(UUID_SERIAL_NUMBER_STRING.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Serial Number String", 
                        "org.bluetooth.characteristic.serial_number_string",
                        UUID_SERIAL_NUMBER_STRING));        
        map.put(UUID_SERVICE_CHANGED.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Service Changed", 
                        "org.bluetooth.characteristic.gatt.service_changed",
                        UUID_SERVICE_CHANGED));
        map.put(UUID_SOFTWARE_REVISION_STRING.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Software Revision String", 
                        "org.bluetooth.characteristic.software_revision_string",
                        UUID_SOFTWARE_REVISION_STRING));    
        map.put(UUID_SUPPORTED_NEW_ALERT_CATEGORY.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Supported New Alert Category", 
                        "org.bluetooth.characteristic.supported_new_alert_category",
                        UUID_SUPPORTED_NEW_ALERT_CATEGORY));          
        map.put(UUID_SUPPORTED_UNREAD_ALERT_CATEGORY.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Supported Unread Alert Category", 
                        "org.bluetooth.characteristic.supported_unread_alert_category",
                        UUID_SUPPORTED_UNREAD_ALERT_CATEGORY));        
        map.put(UUID_SYSTEM_ID.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "System ID", 
                        "org.bluetooth.characteristic.system_id",
                        UUID_SYSTEM_ID));        
        map.put(UUID_TEMPERATURE_MEASUREMENT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Temperature Measurement", 
                        "org.bluetooth.characteristic.temperature_measurement",
                        UUID_TEMPERATURE_MEASUREMENT));         
        map.put(UUID_TEMPERATURE_TYPE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Temperature Type", 
                        "org.bluetooth.characteristic.temperature_type",
                        UUID_TEMPERATURE_TYPE));         
        map.put(UUID_TIME_ACCURACY.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Time Accuracy", 
                        "org.bluetooth.characteristic.time_accuracy",
                        UUID_TIME_ACCURACY));        
        map.put(UUID_TIME_SOURCE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Time Source", 
                        "org.bluetooth.characteristic.time_source",
                        UUID_TIME_SOURCE));          
        map.put(UUID_TIME_UPDATE_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Time Update Control Point", 
                        "org.bluetooth.characteristic.time_update_control_point",
                        UUID_TIME_UPDATE_CONTROL_POINT));          
        map.put(UUID_TIME_UPDATE_CONTROL_POINT.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Time Update State", 
                        "org.bluetooth.characteristic.time_update_state",
                        UUID_TIME_UPDATE_CONTROL_POINT));           
        map.put(UUID_TIME_WITH_DST.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Time with DST", 
                        "org.bluetooth.characteristic.time_with_dst",
                        UUID_TIME_WITH_DST));          
        map.put(UUID_TIME_ZONE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Time Zone", 
                        "org.bluetooth.characteristic.time_zone",
                        UUID_TIME_ZONE));  
        map.put(UUID_TX_POWER_LEVEL.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Tx Power Level", 
                        "org.bluetooth.characteristic.tx_power_level",
                        UUID_TX_POWER_LEVEL));      
        map.put(UUID_UNREAD_ALERT_STATUS.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Unread Alert Status", 
                        "org.bluetooth.characteristic.unread_alert_status",
                        UUID_UNREAD_ALERT_STATUS));        
        map.put(UUID_ADVERTISING_INTERVAL.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Advertising Interval", 
                        "",
                        UUID_ADVERTISING_INTERVAL));        
        map.put(UUID_ACCELERATION_ORIENTATION.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Acceleration/Oritentation", 
                        "",
                        UUID_ACCELERATION_ORIENTATION));         
        map.put(UUID_TEMPERATURE.toLowerCase(), 
                new GattCharacteristicsInfo(
                        "Temperature", 
                        "",
                        UUID_TEMPERATURE));         
        
    }
    
    public static GattCharacteristicsInfo get(String uuid) {
        return map.get(uuid.toLowerCase());
    }
}
