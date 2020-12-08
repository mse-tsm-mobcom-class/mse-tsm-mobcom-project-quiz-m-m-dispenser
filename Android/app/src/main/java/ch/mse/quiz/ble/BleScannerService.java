package ch.mse.quiz.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import ch.mse.quiz.R;
import ch.mse.quiz.permission.PermissionService;
import ch.mse.quiz.printes.ToastPrinter;

import static android.content.ContentValues.TAG;

public class BleScannerService {

    private static final int REQUEST_ENABLE_BT = 1;
    private final Activity activity;
    private final ToastPrinter toastPrinter;
    private final PermissionService permissionService;
    private final PackageManager packageManager;
    private final BleGattCallback bleGattCallback;
    private static final String LOG_TAG = BleScannerService.class.getCanonicalName();
    private BluetoothDevice device;
    private BluetoothGatt deviceGatt;

    public BleScannerService(Activity activity, ToastPrinter toastPrinter, PermissionService permissionService, PackageManager packageManager, BleGattCallback bleGattCallback) {
        this.activity = activity;
        this.toastPrinter = toastPrinter;
        this.permissionService = permissionService;
        this.packageManager = packageManager;
        this.bleGattCallback = bleGattCallback;
    }

    public void startBleScanner() {
        if (!this.permissionService.hasRequiredPermissions()) {
            return;
        }
        boolean hasBle = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        // Or <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
        if (hasBle) {
            Log.d(LOG_TAG, "BLE available");
            BluetoothManager bluetoothManager =
                    (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                Log.d(LOG_TAG, "BLE enabled");
                BleService service = new BleService(bluetoothAdapter.getBluetoothLeScanner());
                service.scan(getScanCallback());
            } else {
                Log.d(TAG, "BLE not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Log.d(LOG_TAG, "BLE not available");
        }
    }

    public BluetoothGatt getDeviceGatt() {
        return deviceGatt;
    }

    public ScanCallback getScanCallback() {
        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (null != result && !bleGattCallback.isConnected() && null == device) {
                    device = result.getDevice();
                    if (null != device) {
                        Log.i(LOG_TAG, device.getAddress());
                        deviceGatt = device.connectGatt(activity, false, bleGattCallback,
                                BluetoothDevice.TRANSPORT_AUTO);
                        toastPrinter.print(activity.getString(R.string.ble_connected));
                    }
                }
            }
        };
    }
}
