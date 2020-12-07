// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class BleGattCallbackTest extends TestCase {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    private BleGattCallback bleGattCallback;
    @Mock
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic mmDispenserStateCharacteristic;
    private BluetoothGattCharacteristic mmDispenserDispenseCharacteristic;
    private BluetoothGattCharacteristic mmDispenserFillingLevelCharacteristic;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        bleGattCallback = BleGattCallback.getInstance();
        BluetoothGattService bluetoothGattService = new BluetoothGattService(UUID.fromString("113A0001-FD33-441B-9A57-E9F1C29633D3"), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mmDispenserStateCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("113A0002-FD33-441B-9A57-E9F1C29633D3"), BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_READ);
        mmDispenserDispenseCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("113A0003-FD33-441B-9A57-E9F1C29633D3"), BluetoothGattCharacteristic.PERMISSION_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        mmDispenserFillingLevelCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("113A0004-FD33-441B-9A57-E9F1C29633D3"), BluetoothGattCharacteristic.PERMISSION_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        byte[] dispenseState = {0x01};
        byte[] fillingLevel = {0x000F};
        BluetoothGattDescriptor descriptor1 = new BluetoothGattDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattDescriptor.PERMISSION_READ);
        BluetoothGattDescriptor descriptor2 = new BluetoothGattDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattDescriptor.PERMISSION_READ);
        mmDispenserStateCharacteristic.setValue(dispenseState);
        mmDispenserStateCharacteristic.addDescriptor(descriptor1);
        mmDispenserFillingLevelCharacteristic.setValue(fillingLevel);
        mmDispenserFillingLevelCharacteristic.addDescriptor(descriptor2);
        bluetoothGattService.addCharacteristic(mmDispenserStateCharacteristic);
        bluetoothGattService.addCharacteristic(mmDispenserDispenseCharacteristic);
        bluetoothGattService.addCharacteristic(mmDispenserFillingLevelCharacteristic);
        List<BluetoothGattService> services = new ArrayList<>();
        services.add(bluetoothGattService);
        Mockito.when(gatt.getServices()).thenReturn(services);
    }

    @Test
    public void onDescriptorWriteDispenserState() {
        bleGattCallback.onServicesDiscovered(gatt, BluetoothGatt.GATT_SUCCESS);
        BluetoothGattDescriptor descriptor = mmDispenserStateCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        bleGattCallback.onDescriptorWrite(gatt, descriptor, 0);
        assertTrue(true);
    }

    @Test
    public void onDescriptorWriteFillingLevel() {
        bleGattCallback.onServicesDiscovered(gatt, BluetoothGatt.GATT_SUCCESS);
        BluetoothGattDescriptor descriptor = mmDispenserFillingLevelCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        bleGattCallback.onDescriptorWrite(gatt, descriptor, 0);
        assertTrue(true);
    }

    @Test
    public void onConnectionStateChange() {
        bleGattCallback.onConnectionStateChange(gatt, 1, BluetoothGatt.STATE_CONNECTED);
        assertTrue("connection state change failed", true);
        bleGattCallback.onConnectionStateChange(gatt, 1, BluetoothGatt.STATE_DISCONNECTED);
        assertTrue("disconnection state change failed", true);
    }

    @Test
    public void onServicesDiscovered() {
        bleGattCallback.onServicesDiscovered(gatt, BluetoothGatt.GATT_SUCCESS);
        assertTrue("failed on service discovered", true);
    }

    @Test
    public void dispense() {
        bleGattCallback.onServicesDiscovered(gatt, BluetoothGatt.GATT_SUCCESS);
        bleGattCallback.dispense();
        assertTrue("failed to dispense", true);
    }

    @Test
    public void onCharacteristicRead() {
        bleGattCallback.onCharacteristicRead(gatt, mmDispenserStateCharacteristic, BluetoothGatt.GATT_SUCCESS);
        assertTrue("failed to call onCharacteristicRead", true);
    }

    @Test
    public void onCharacteristicWrite() {
        bleGattCallback.onCharacteristicWrite(gatt, mmDispenserDispenseCharacteristic, BluetoothGatt.GATT_SUCCESS);
        assertTrue("failed to call onCharacteristicWrite", true);
    }

    @Test
    public void onCharacteristicChanged() {
        byte[] state = {0x00, 0x01};
        byte[] flevel = {0x000E};
        mmDispenserStateCharacteristic.setValue(state);
        mmDispenserFillingLevelCharacteristic.setValue(flevel);
        bleGattCallback.onCharacteristicChanged(gatt, mmDispenserStateCharacteristic);
        assertTrue(bleGattCallback.isDispenserState());
        bleGattCallback.onCharacteristicChanged(gatt, mmDispenserFillingLevelCharacteristic);
        assertEquals(14L, bleGattCallback.getFillingLevel());
    }

    @Test
    public void isDispenserState() {
        assertFalse("failed to read dispenser state", bleGattCallback.isDispenserState());
    }

    @Test
    public void isConnected() {
        assertFalse("failed to read is connected", bleGattCallback.isConnected());
    }

    @Test
    public void setFillingLevel() {
        byte[] fillingLevelByte = {0x000F};
        bleGattCallback.setFillingLevel(fillingLevelByte);
        assertEquals(15, bleGattCallback.getFillingLevel());
    }

    @Test
    public void convertBytesToHex() {
        byte[] fillingLevelByte = {0x000F};
        String value = bleGattCallback.convertBytesToHex(fillingLevelByte);
        assertEquals("0f", value);
    }

    @Test
    public void getFillingLevelPercentage() {
        byte[] fillingLevelByte = {0x000F};
        bleGattCallback.setFillingLevel(fillingLevelByte);
        double fillingLevelPercentage = bleGattCallback.getFillingLevelPercentage();
        assertTrue(28 < fillingLevelPercentage);
    }
}