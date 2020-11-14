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
    private static final String TAG = BleGattCallback.class.getSimpleName();
    private final UUID mmDispenserStateCharacteristicUuid = UUID.fromString("113A0002-FD33-441B-9A57-E9F1C29633D3");
    private final UUID mmDispenserDispenseCharacteristicUuid = UUID.fromString("113A0003-FD33-441B-9A57-E9F1C29633D3");
    private final UUID clientCharacteristicConfigurationUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final byte[] dispenseValue = {0x01};
    private BluetoothGattCharacteristic mmDispenserStateCharacteristic = null;
    private BluetoothGattCharacteristic mmDispenserDispenseCharacteristic = null;
    private BluetoothGatt gatt;
    private boolean dispenserState = false;
    private boolean isConnected = false;

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "Connected to GATT server.");
            gatt.discoverServices();
            this.isConnected = true;
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
                    }
                }
            }
        }
    }

    public void dispense() {
        if (null != mmDispenserDispenseCharacteristic && null != gatt) {
            mmDispenserDispenseCharacteristic.setValue(dispenseValue);
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
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
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
        }
    }

    public boolean isDispenserState() {
        return dispenserState;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
