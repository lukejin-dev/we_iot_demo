package com.ies.blelib.service;

import java.util.HashMap;

public class GattServiceDb {
    public static final String UUID_ALERT_NOTIFICATION = 
            "00001811-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BATTERY = 
            "0000180F-0000-1000-8000-00805f9b34fb";
    public static final String UUID_BLOOD_PRESSURE = 
            "00001810-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CURRENT_TIME = 
            "00001805-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CYCLING_POWER = 
            "00001818-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CYCLING_SPEED_AND_CADENCE = 
            "00001816-0000-1000-8000-00805f9b34fb";
    public static final String UUID_DEVICE_INFORMATION = 
            "0000180A-0000-1000-8000-00805f9b34fb";
    public static final String UUID_GENERIC_ACCESS = 
            "00001800-0000-1000-8000-00805f9b34fb";
    public static final String UUID_GENERIC_ATTRIBUTE = 
            "00001801-0000-1000-8000-00805f9b34fb";
    public static final String UUID_GLUCOSE = 
            "00001808-0000-1000-8000-00805f9b34fb";
    public static final String UUID_HEALTH_THERMOMETER = 
            "00001809-0000-1000-8000-00805f9b34fb";
    public static final String UUID_HEART_RATE = 
            "0000180D-0000-1000-8000-00805f9b34fb";
    public static final String UUID_HUMAN_INTERFACE_DEVICE = 
            "00001812-0000-1000-8000-00805f9b34fb";
    public static final String UUID_IMMEDIATE_ALERT = 
            "00001802-0000-1000-8000-00805f9b34fb";
    public static final String UUID_LINK_LOSS = 
            "00001803-0000-1000-8000-00805f9b34fb";
    public static final String UUID_LOCATION_AND_NAVIGATION = 
            "00001819-0000-1000-8000-00805f9b34fb";
    public static final String UUID_NEXT_DST_CHANGE = 
            "00001807-0000-1000-8000-00805f9b34fb";
    public static final String UUID_PHONE_ALERT_STATUS = 
            "0000180E-0000-1000-8000-00805f9b34fb";
    public static final String UUID_REFERENCE_TIME_UPDATE = 
            "00001806-0000-1000-8000-00805f9b34fb";
    public static final String UUID_RUNNING_SPEED_AND_CADENCE = 
            "00001814-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SCAN_PARAMETERS = 
            "00001813-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TX_POWER = 
            "00001804-0000-1000-8000-00805f9b34fb";
    public static final String UUID_ADVERTISING_INTERVAL = 
            "00001930-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TEMPERATURE = 
            "00001910-0000-1000-8000-00805f9b34fb";
    public static final String UUID_ACCELERATION = 
            "00001920-0000-1000-8000-00805f9b34fb";
    
    
    //
    // TI Service
    //
    public static final String UUID_TI_SIMPLE_KEY_SERVICE =
            "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String UUID_TI_THERMOMETER = 
            "f000aa00-0451-4000-b000-000000000000";
    public static final String UUID_TI_ACCELEROMETER =
            "f000aa10-0451-4000-b000-000000000000";
    public static final String UUID_TI_HUMIDITY =
            "f000aa20-0451-4000-b000-000000000000";
    public static final String UUID_TI_MAGNETOMETER = 
            "f000aa30-0451-4000-b000-000000000000";
    public static final String UUID_TI_BAROMETER = 
            "f000aa40-0451-4000-b000-000000000000";
    public static final String UUID_TI_GYROSCOPE =
            "f000aa50-0451-4000-b000-000000000000";
    
    
    
    private static HashMap<String, GattServiceInfo> map = 
            new HashMap<String, GattServiceInfo>();
    
    static {
        map.put(UUID_ALERT_NOTIFICATION.toLowerCase(), 
                new GattServiceInfo(
                        "Alert Notification Service", 
                        "org.bluetooth.service.alert_notification",
                        UUID_ALERT_NOTIFICATION));
        map.put(UUID_BATTERY.toLowerCase(), 
                new GattServiceInfo(
                        "Battery Service",
                        "org.bluetooth.service.battery_service",
                        UUID_BATTERY));
        map.put(UUID_BLOOD_PRESSURE.toLowerCase(), 
                new GattServiceInfo(
                        "Blood Pressure",
                        "org.bluetooth.service.blood_pressure",
                        UUID_BATTERY));
        map.put(UUID_CURRENT_TIME.toLowerCase(), 
                new GattServiceInfo(
                        "Current Time Service",
                        "org.bluetooth.service.current_time",
                        UUID_CURRENT_TIME));
        map.put(UUID_CYCLING_POWER.toLowerCase(), 
                new GattServiceInfo(
                        "Cycling Power",
                        "org.bluetooth.service.cycling_power",
                        UUID_CYCLING_POWER));
        map.put(UUID_CYCLING_SPEED_AND_CADENCE.toLowerCase(), 
                new GattServiceInfo(
                        "Cycling Speed and Cadence",
                        "org.bluetooth.service.cycling_speed_and_cadence",
                        UUID_CYCLING_SPEED_AND_CADENCE));          
        map.put(UUID_DEVICE_INFORMATION.toLowerCase(), 
                new GattServiceInfo(
                        "Device Information",
                        "org.bluetooth.service.device_information",
                        UUID_DEVICE_INFORMATION));         
        map.put(UUID_GENERIC_ACCESS.toLowerCase(), 
                new GattServiceInfo(
                        "Generic Access",
                        "org.bluetooth.service.generic_access",
                        UUID_GENERIC_ACCESS)); 
        map.put(UUID_GENERIC_ATTRIBUTE.toLowerCase(), 
                new GattServiceInfo(
                        "Generic Attribute",
                        "org.bluetooth.service.generic_attribute",
                        UUID_GENERIC_ATTRIBUTE));         
        map.put(UUID_GLUCOSE.toLowerCase(), 
                new GattServiceInfo(
                        "Glucose",
                        "org.bluetooth.service.glucose",
                        UUID_GLUCOSE));         
        map.put(UUID_HEALTH_THERMOMETER.toLowerCase(), 
                new GattServiceInfo(
                        "Health Thermometer",
                        "org.bluetooth.service.health_thermometer",
                        UUID_HEALTH_THERMOMETER));     
        map.put(UUID_HEART_RATE.toLowerCase(), 
                new GattServiceInfo(
                        "Heart Rate",
                        "org.bluetooth.service.heart_rate",
                        UUID_HEART_RATE));        
        map.put(UUID_HUMAN_INTERFACE_DEVICE.toLowerCase(), 
                new GattServiceInfo(
                        "Human Interface Device",
                        "org.bluetooth.service.human_interface_device",
                        UUID_HUMAN_INTERFACE_DEVICE));            
        map.put(UUID_IMMEDIATE_ALERT.toLowerCase(), 
                new GattServiceInfo(
                        "Immediate Alert",
                        "org.bluetooth.service.immediate_alert",
                        UUID_IMMEDIATE_ALERT));
        map.put(UUID_LINK_LOSS.toLowerCase(), 
                new GattServiceInfo(
                        "Link Loss",
                        "org.bluetooth.service.link_loss",
                        UUID_LINK_LOSS));
        map.put(UUID_LOCATION_AND_NAVIGATION.toLowerCase(), 
                new GattServiceInfo(
                        "Location and Navigation",
                        "org.bluetooth.service.location_and_navigation",
                        UUID_LOCATION_AND_NAVIGATION));        
        map.put(UUID_NEXT_DST_CHANGE.toLowerCase(), 
                new GattServiceInfo(
                        "Next DST Change Service",
                        "org.bluetooth.service.next_dst_change",
                        UUID_NEXT_DST_CHANGE));        
        map.put(UUID_PHONE_ALERT_STATUS.toLowerCase(), 
                new GattServiceInfo(
                        "Phone Alert Status Service",
                        "org.bluetooth.service.phone_alert_status",
                        UUID_PHONE_ALERT_STATUS));   
        map.put(UUID_REFERENCE_TIME_UPDATE.toLowerCase(), 
                new GattServiceInfo(
                        "Reference Time Update Service",
                        "org.bluetooth.service.reference_time_update",
                        UUID_REFERENCE_TIME_UPDATE)); 
        map.put(UUID_RUNNING_SPEED_AND_CADENCE.toLowerCase(), 
                new GattServiceInfo(
                        "Running Speed and Cadence",
                        "org.bluetooth.service.running_speed_and_cadence",
                        UUID_RUNNING_SPEED_AND_CADENCE)); 
        map.put(UUID_SCAN_PARAMETERS.toLowerCase(), 
                new GattServiceInfo(
                        "Scan Parameters",
                        "org.bluetooth.service.scan_parameters",
                        UUID_SCAN_PARAMETERS)); 
        map.put(UUID_TX_POWER.toLowerCase(), 
                new GattServiceInfo(
                        "Tx Power",
                        "org.bluetooth.service.tx_power",
                        UUID_TX_POWER));         
        map.put(UUID_ADVERTISING_INTERVAL.toLowerCase(), 
                new GattServiceInfo(
                        "Advertising Interval Service",
                        "",
                        UUID_ADVERTISING_INTERVAL));
        map.put(UUID_TEMPERATURE.toLowerCase(), 
                new GattServiceInfo(
                        "Temperature Service",
                        "",
                        UUID_TEMPERATURE));        
        map.put(UUID_ACCELERATION.toLowerCase(), 
                new GattServiceInfo(
                        "Acceleration/Orientation Service",
                        "",
                        UUID_ACCELERATION));   
        map.put(UUID_TI_SIMPLE_KEY_SERVICE.toLowerCase(), 
                new GattServiceInfo(
                        "TI Simple Key Service",
                        "",
                        UUID_TI_SIMPLE_KEY_SERVICE));      
        map.put(UUID_TI_THERMOMETER.toLowerCase(), 
                new GattServiceInfo(
                        "TI Thermometer",
                        "",
                        UUID_TI_THERMOMETER));          
        map.put(UUID_TI_ACCELEROMETER.toLowerCase(), 
                new GattServiceInfo(
                        "TI Accelerator",
                        "",
                        UUID_TI_ACCELEROMETER));         
        map.put(UUID_TI_HUMIDITY.toLowerCase(), 
                new GattServiceInfo(
                        "TI Hummidity",
                        "",
                        UUID_TI_HUMIDITY));          
        map.put(UUID_TI_BAROMETER.toLowerCase(), 
                new GattServiceInfo(
                        "TI Barometer",
                        "",
                        UUID_TI_BAROMETER));         
        map.put(UUID_TI_GYROSCOPE.toLowerCase(), 
                new GattServiceInfo(
                        "TI Gyroscope",
                        "",
                        UUID_TI_GYROSCOPE));         
        map.put(UUID_TI_MAGNETOMETER.toLowerCase(), 
                new GattServiceInfo(
                        "TI Magnetometer",
                        "",
                        UUID_TI_MAGNETOMETER));          
        
    }
    
    public static GattServiceInfo get(String uuid) {
        return map.get(uuid.toLowerCase());
    }
}
