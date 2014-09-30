package com.ies.blelib.service;

import java.util.HashMap;

public class GattServiceDb {
    private static final String UUID_ALERT_NOTIFICATION = 
            "00001811-0000-1000-8000-00805f9b34fb";
    private static final String UUID_BATTERY = 
            "0000180F-0000-1000-8000-00805f9b34fb";
    private static final String UUID_BLOOD_PRESSURE = 
            "00001810-0000-1000-8000-00805f9b34fb";
    private static final String UUID_CURRENT_TIME = 
            "00001805-0000-1000-8000-00805f9b34fb";
    private static final String UUID_CYCLING_POWER = 
            "00001818-0000-1000-8000-00805f9b34fb";
    private static final String UUID_CYCLING_SPEED_AND_CADENCE = 
            "00001816-0000-1000-8000-00805f9b34fb";
    private static final String UUID_DEVICE_INFORMATION = 
            "0000180A-0000-1000-8000-00805f9b34fb";
    private static final String UUID_GENERIC_ACCESS = 
            "00001800-0000-1000-8000-00805f9b34fb";
    private static final String UUID_GENERIC_ATTRIBUTE = 
            "00001801-0000-1000-8000-00805f9b34fb";
    private static final String UUID_GLUCOSE = 
            "00001808-0000-1000-8000-00805f9b34fb";
    private static final String UUID_HEALTH_THERMOMETER = 
            "00001809-0000-1000-8000-00805f9b34fb";
    private static final String UUID_HEART_RATE = 
            "0000180D-0000-1000-8000-00805f9b34fb";
    private static final String UUID_HUMAN_INTERFACE_DEVICE = 
            "00001812-0000-1000-8000-00805f9b34fb";
    private static final String UUID_IMMEDIATE_ALERT = 
            "00001802-0000-1000-8000-00805f9b34fb";
    private static final String UUID_LINK_LOSS = 
            "00001803-0000-1000-8000-00805f9b34fb";
    private static final String UUID_LOCATION_AND_NAVIGATION = 
            "00001819-0000-1000-8000-00805f9b34fb";
    private static final String UUID_NEXT_DST_CHANGE = 
            "00001807-0000-1000-8000-00805f9b34fb";
    private static final String UUID_PHONE_ALERT_STATUS = 
            "0000180E-0000-1000-8000-00805f9b34fb";
    private static final String UUID_REFERENCE_TIME_UPDATE = 
            "00001806-0000-1000-8000-00805f9b34fb";
    private static final String UUID_RUNNING_SPEED_AND_CADENCE = 
            "00001814-0000-1000-8000-00805f9b34fb";
    private static final String UUID_SCAN_PARAMETERS = 
            "00001813-0000-1000-8000-00805f9b34fb";
    private static final String UUID_TX_POWER = 
            "00001804-0000-1000-8000-00805f9b34fb";
    private static final String UUID_ADVERTISING_INTERVAL = 
            "00001930-0000-1000-8000-00805f9b34fb";
    private static final String UUID_TEMPERATURE = 
            "00001910-0000-1000-8000-00805f9b34fb";
    private static final String UUID_ACCELERATION = 
            "00001920-0000-1000-8000-00805f9b34fb";
    
    private static HashMap<String, GattService> map = 
            new HashMap<String, GattService>();
    
    static {
        map.put(UUID_ALERT_NOTIFICATION.toLowerCase(), 
                new GattService(
                        "Alert Notification Service", 
                        "org.bluetooth.service.alert_notification",
                        UUID_ALERT_NOTIFICATION));
        map.put(UUID_BATTERY.toLowerCase(), 
                new GattService(
                        "Battery Service",
                        "org.bluetooth.service.battery_service",
                        UUID_BATTERY));
        map.put(UUID_BLOOD_PRESSURE.toLowerCase(), 
                new GattService(
                        "Blood Pressure",
                        "org.bluetooth.service.blood_pressure",
                        UUID_BATTERY));
        map.put(UUID_CURRENT_TIME.toLowerCase(), 
                new GattService(
                        "Current Time Service",
                        "org.bluetooth.service.current_time",
                        UUID_CURRENT_TIME));
        map.put(UUID_CYCLING_POWER.toLowerCase(), 
                new GattService(
                        "Cycling Power",
                        "org.bluetooth.service.cycling_power",
                        UUID_CYCLING_POWER));
        map.put(UUID_CYCLING_SPEED_AND_CADENCE.toLowerCase(), 
                new GattService(
                        "Cycling Speed and Cadence",
                        "org.bluetooth.service.cycling_speed_and_cadence",
                        UUID_CYCLING_SPEED_AND_CADENCE));          
        map.put(UUID_DEVICE_INFORMATION.toLowerCase(), 
                new GattService(
                        "Device Information",
                        "org.bluetooth.service.device_information",
                        UUID_DEVICE_INFORMATION));         
        map.put(UUID_GENERIC_ACCESS.toLowerCase(), 
                new GattService(
                        "Generic Access",
                        "org.bluetooth.service.generic_access",
                        UUID_GENERIC_ACCESS)); 
        map.put(UUID_GENERIC_ATTRIBUTE.toLowerCase(), 
                new GattService(
                        "Generic Attribute",
                        "org.bluetooth.service.generic_attribute",
                        UUID_GENERIC_ATTRIBUTE));         
        map.put(UUID_GLUCOSE.toLowerCase(), 
                new GattService(
                        "Glucose",
                        "org.bluetooth.service.glucose",
                        UUID_GLUCOSE));         
        map.put(UUID_HEALTH_THERMOMETER.toLowerCase(), 
                new GattService(
                        "Health Thermometer",
                        "org.bluetooth.service.health_thermometer",
                        UUID_HEALTH_THERMOMETER));     
        map.put(UUID_HEART_RATE.toLowerCase(), 
                new GattService(
                        "Heart Rate",
                        "org.bluetooth.service.heart_rate",
                        UUID_HEART_RATE));        
        map.put(UUID_HUMAN_INTERFACE_DEVICE.toLowerCase(), 
                new GattService(
                        "Human Interface Device",
                        "org.bluetooth.service.human_interface_device",
                        UUID_HUMAN_INTERFACE_DEVICE));            
        map.put(UUID_IMMEDIATE_ALERT.toLowerCase(), 
                new GattService(
                        "Immediate Alert",
                        "org.bluetooth.service.immediate_alert",
                        UUID_IMMEDIATE_ALERT));
        map.put(UUID_LINK_LOSS.toLowerCase(), 
                new GattService(
                        "Link Loss",
                        "org.bluetooth.service.link_loss",
                        UUID_LINK_LOSS));
        map.put(UUID_LOCATION_AND_NAVIGATION.toLowerCase(), 
                new GattService(
                        "Location and Navigation",
                        "org.bluetooth.service.location_and_navigation",
                        UUID_LOCATION_AND_NAVIGATION));        
        map.put(UUID_NEXT_DST_CHANGE.toLowerCase(), 
                new GattService(
                        "Next DST Change Service",
                        "org.bluetooth.service.next_dst_change",
                        UUID_NEXT_DST_CHANGE));        
        map.put(UUID_PHONE_ALERT_STATUS.toLowerCase(), 
                new GattService(
                        "Phone Alert Status Service",
                        "org.bluetooth.service.phone_alert_status",
                        UUID_PHONE_ALERT_STATUS));   
        map.put(UUID_REFERENCE_TIME_UPDATE.toLowerCase(), 
                new GattService(
                        "Reference Time Update Service",
                        "org.bluetooth.service.reference_time_update",
                        UUID_REFERENCE_TIME_UPDATE)); 
        map.put(UUID_RUNNING_SPEED_AND_CADENCE.toLowerCase(), 
                new GattService(
                        "Running Speed and Cadence",
                        "org.bluetooth.service.running_speed_and_cadence",
                        UUID_RUNNING_SPEED_AND_CADENCE)); 
        map.put(UUID_SCAN_PARAMETERS.toLowerCase(), 
                new GattService(
                        "Scan Parameters",
                        "org.bluetooth.service.scan_parameters",
                        UUID_SCAN_PARAMETERS)); 
        map.put(UUID_TX_POWER.toLowerCase(), 
                new GattService(
                        "Tx Power",
                        "org.bluetooth.service.tx_power",
                        UUID_TX_POWER));         
        map.put(UUID_ADVERTISING_INTERVAL.toLowerCase(), 
                new GattService(
                        "Advertising Interval Service",
                        "",
                        UUID_ADVERTISING_INTERVAL));
        map.put(UUID_TEMPERATURE.toLowerCase(), 
                new GattService(
                        "Temperature Service",
                        "",
                        UUID_TEMPERATURE));        
        map.put(UUID_ACCELERATION.toLowerCase(), 
                new GattService(
                        "Acceleration/Orientation Service",
                        "",
                        UUID_ACCELERATION));         
    }
    
    public static GattService get(String uuid) {
        return map.get(uuid.toLowerCase());
    }
}
