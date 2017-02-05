package com.dable.bluetooth;

import java.util.HashMap;

public class GattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String QUATERNION_MEASUREMENT         = "6e400003-b5a3-f393-e0A9-e50e24dcca9e";
    public static String CLIENT_CHARACTERISTIC_CONFIG   = "00002902-0000-1000-8000-00805f9b34fb";
    //public static String NOTIFY_DESC                    = "6e402902-b5a3-f393-e0a9-e50e24dcca9e";

    static {
        // Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "PRIMARY SERVICE");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "GENERIC ATTRIBUTE");
        attributes.put("00001530-1212-efde-1523-785feabcd123", "CUSTOM SERVICE 1");
        attributes.put("6e400001-b5a3-f393-e0a9-e50e24dcca9e", "CUSTOM SERVICE 2");
        // Characteristics.
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "DEVICE NAME");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "APPEARANCE");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "PERIPHERAL");

        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "SERVICE CHANGED");

        attributes.put("00001531-1212-efde-1523-785feabcd123", "CUSTOM CHARACTERISTIC-W/N");
        attributes.put("00001532-1212-efde-1523-785feabcd123", "CUSTOM CHARACTERISTIC-W");

        attributes.put("6e400002-b5a3-f393-e0a9-e50e24dcca9e", "CUSTOM CHARACTERISTIC-W");
        attributes.put("6e400003-b5a3-f393-e0a9-e50e24dcca9e", "CUSTOM CHARACTERISTIC-N");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
