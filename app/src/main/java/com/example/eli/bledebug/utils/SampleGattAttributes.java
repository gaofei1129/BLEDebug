/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.eli.bledebug.utils;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static String SIMPLE_PROFILE_SERVICE = "0000fff0-0000-1000-8000-00805f9b34fb";
    public static String CUSTOM_DATA_TRANSFER = "0000fff6-0000-1000-8000-00805f9b34fb";
    public static String CUSTOM_NOTIFICATION = "0000fff7-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access Service");
        attributes.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute Service");
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        attributes.put("0000ffe0-0000-1000-8000-00805f9b34fb", "Simple Keys Service");
        attributes.put(SIMPLE_PROFILE_SERVICE, "Simple Profile Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        attributes.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        attributes.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag");
        attributes.put("00002a03-0000-1000-8000-00805f9b34fb", "Reconnection Address");
        attributes.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
        attributes.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");
        attributes.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID");
        attributes.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
        attributes.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
        attributes.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        attributes.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        attributes.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        attributes.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE 11073-20601 Regulatory Certification Data List");
        attributes.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID");
        attributes.put("0000ffe1-0000-1000-8000-00805f9b34fb", "Key press state");
        attributes.put("0000fff1-0000-1000-8000-00805f9b34fb", "Simple Profile Characteristic 1");
        attributes.put("0000fff2-0000-1000-8000-00805f9b34fb", "Simple Profile Characteristic 2");
        attributes.put("0000fff3-0000-1000-8000-00805f9b34fb", "Simple Profile Characteristic 3");
        attributes.put("0000fff4-0000-1000-8000-00805f9b34fb", "Simple Profile Characteristic 4");
        attributes.put("0000fff5-0000-1000-8000-00805f9b34fb", "Simple Profile Characteristic 5");
        attributes.put(CUSTOM_DATA_TRANSFER, "Custom Data Transfer");
        attributes.put(CUSTOM_NOTIFICATION, "Custom Notification");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
