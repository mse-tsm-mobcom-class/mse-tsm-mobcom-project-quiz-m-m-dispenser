// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

// Based on https://github.com/tamberg/mse-tsm-mobcom licensed under CCO 1.0

package ch.mse.quiz.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BleGattCallback extends BluetoothGattCallback {

    private static BleGattCallback instance = null;

    private static final String TAG = BleGattCallback.class.getSimpleName();
    private final UUID mmDispenserStateCharacteristicUuid = UUID.fromString("113A0002-FD33-441B-9A57-E9F1C29633D3");
    private final UUID mmDispenserDispenseCharacteristicUuid = UUID.fromString("113A0003-FD33-441B-9A57-E9F1C29633D3");
    private final UUID mmDispenserFillingLevelCharacteristicUuid = UUID.fromString("113A0004-FD33-441B-9A57-E9F1C29633D3");
    private final UUID clientCharacteristicConfigurationUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final byte[] dispenseValueSend = {0x01};
    private static final byte[] dispenseValue = {0x00, 0x01};
    private BluetoothGattCharacteristic mmDispenserStateCharacteristic = null;
    private BluetoothGattCharacteristic mmDispenserDispenseCharacteristic = null;
    private BluetoothGattCharacteristic mmDispenserFillingLevelCharacteristic = null;
    private BluetoothGatt gatt;
    private boolean dispenserState = false;
    private boolean isConnected = false;
    private int fillingLevel = 0;
    private boolean isDispenserStateNotificationEnabled = false;
    private boolean isFillingLevelNotificationEnabled = false;

    private boolean isLow = false;
    private boolean isTheft = false;

    private BleGattCallback() {
    }

    public static BleGattCallback getInstance() {
        if (null == instance) {
            instance = new BleGattCallback();
        }
        return instance;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "Connected to GATT server.");
            gatt.discoverServices();
            this.isConnected = true;
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            this.isConnected = false;
            isFillingLevelNotificationEnabled = false;
            isDispenserStateNotificationEnabled = false;
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            this.gatt = gatt;
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService service : services) {
                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    if (characteristic.getUuid().equals(mmDispenserStateCharacteristicUuid)) {
                        mmDispenserStateCharacteristic = characteristic;
                        this.enableDispenserStateNotification();
                    } else if (characteristic.getUuid().equals(mmDispenserDispenseCharacteristicUuid)) {
                        mmDispenserDispenseCharacteristic = characteristic;
                    } else if (characteristic.getUuid().equals(mmDispenserFillingLevelCharacteristicUuid)) {
                        mmDispenserFillingLevelCharacteristic = characteristic;
                        this.enableFillingLevelNotification();
                    }
                }
            }
        }
    }

    public void dispense() {
        if (null != mmDispenserDispenseCharacteristic && null != gatt) {
            mmDispenserDispenseCharacteristic.setValue(dispenseValueSend);
            mmDispenserDispenseCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            gatt.writeCharacteristic(mmDispenserDispenseCharacteristic);
        }
    }

    public void enableDispenserStateNotification() {
        if (null != mmDispenserStateCharacteristic && null != gatt) {
            gatt.setCharacteristicNotification(mmDispenserStateCharacteristic, true);
            BluetoothGattDescriptor descriptor = mmDispenserStateCharacteristic.getDescriptor(clientCharacteristicConfigurationUuid);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        if (null != mmDispenserStateCharacteristic && descriptor.getCharacteristic().getUuid() == mmDispenserStateCharacteristic.getUuid()) {
            isDispenserStateNotificationEnabled = true;
            if (!isFillingLevelNotificationEnabled) {
                enableFillingLevelNotification();
            }
        } else if (null != mmDispenserFillingLevelCharacteristic && descriptor.getCharacteristic().getUuid() == mmDispenserFillingLevelCharacteristic.getUuid()) {
            isFillingLevelNotificationEnabled = true;
            if (!isDispenserStateNotificationEnabled) {
                enableDispenserStateNotification();
            }
        }
    }

    public void enableFillingLevelNotification() {
        if (null != mmDispenserFillingLevelCharacteristic && null != gatt) {
            gatt.setCharacteristicNotification(mmDispenserFillingLevelCharacteristic, true);
            BluetoothGattDescriptor descriptor = mmDispenserFillingLevelCharacteristic.getDescriptor(clientCharacteristicConfigurationUuid);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        Log.d(TAG, "Characteristic " + characteristic.getUuid() + " written");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        if (characteristic.getUuid().equals(mmDispenserStateCharacteristicUuid)) {
            this.dispenserState = Arrays.equals(characteristic.getValue(), dispenseValue);
        } else if (characteristic.getUuid().equals(mmDispenserFillingLevelCharacteristicUuid)) {
            setFillingLevel(characteristic.getValue());
        }
    }

    public int getFillingLevel() {
        return fillingLevel;
    }

    public double getFillingLevelPercentage() {
        double dispenserHeightCm = 210;
        double fillingLevelPercentage = (1 - (fillingLevel / dispenserHeightCm)) * 100;
        if (0 > fillingLevelPercentage) {
            fillingLevelPercentage = 0;
        } else if (100 < fillingLevelPercentage) {
            fillingLevelPercentage = 100;
        }
        checkTheft(fillingLevelPercentage);
        isLow = 20 >= fillingLevelPercentage;
        return fillingLevelPercentage;
    }

    private void checkTheft(double currentPercentage) {
        isTheft = 0 == currentPercentage && !isLow;
    }

    public boolean isDispenserState() {
        return dispenserState;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setFillingLevel(byte[] value) {
        fillingLevel = Integer.parseInt(convertBytesToHex(value), 16);
    }

    public boolean isTheft() {
        return isTheft;
    }

    //Source: https://mkyong.com/java/java-convert-byte-to-int-and-vice-versa/
    public String convertBytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
            result.append(String.format("%02x", temp));
        }
        return result.toString();
    }
}
