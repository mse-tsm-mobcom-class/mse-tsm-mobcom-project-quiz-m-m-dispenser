package ch.mse.quiz.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
public class BleGattCallbackTest extends TestCase {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    private BleGattCallback bleGattCallback;
    @Mock
    private BluetoothGatt gatt;
    private BluetoothGattCharacteristic mmDispenserStateCharacteristic;
    private BluetoothGattCharacteristic mmDispenserDispenseCharacteristic;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        bleGattCallback = new BleGattCallback();
        BluetoothGattService bluetoothGattService = new BluetoothGattService(UUID.fromString("113A0001-FD33-441B-9A57-E9F1C29633D3"), BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mmDispenserStateCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("113A0002-FD33-441B-9A57-E9F1C29633D3"), BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_READ);
        mmDispenserDispenseCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("113A0003-FD33-441B-9A57-E9F1C29633D3"), BluetoothGattCharacteristic.PERMISSION_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        byte[] dispenseState = {0x01};
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattDescriptor.PERMISSION_READ);
        mmDispenserStateCharacteristic.setValue(dispenseState);
        mmDispenserStateCharacteristic.addDescriptor(descriptor);
        bluetoothGattService.addCharacteristic(mmDispenserStateCharacteristic);
        bluetoothGattService.addCharacteristic(mmDispenserDispenseCharacteristic);
        List<BluetoothGattService> services = new ArrayList<>();
        services.add(bluetoothGattService);
        Mockito.when(gatt.getServices()).thenReturn(services);
    }

    @Test
    public void onConnectionStateChange() {
        bleGattCallback.onConnectionStateChange(gatt, 1, BluetoothGatt.STATE_CONNECTED);
        assertTrue("connection state change failed", true);
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
        byte[] state = {0x01};
        mmDispenserStateCharacteristic.setValue(state);
        bleGattCallback.onCharacteristicChanged(gatt, mmDispenserStateCharacteristic);
        assertTrue(bleGattCallback.isDispenserState());
    }

    @Test
    public void isDispenserState() {
        assertFalse("failed to read dispenser state", bleGattCallback.isDispenserState());
    }

    @Test
    public void isConnected() {
        assertFalse("failed to read is connected", bleGattCallback.isConnected());
    }
}