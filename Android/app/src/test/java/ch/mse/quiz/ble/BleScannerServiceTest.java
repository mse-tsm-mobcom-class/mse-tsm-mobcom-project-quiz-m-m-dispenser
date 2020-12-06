package ch.mse.quiz.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ch.mse.quiz.MainActivity;
import ch.mse.quiz.permission.PermissionService;
import ch.mse.quiz.printes.ToastPrinter;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class BleScannerServiceTest extends TestCase {

    @Test
    public void startBleScanner() {
        Activity activity = PowerMockito.mock(MainActivity.class);
        ToastPrinter toastPrinter = PowerMockito.mock(ToastPrinter.class);
        BleGattCallback bleGattCallback = PowerMockito.mock(BleGattCallback.class);
        PermissionService permissionService = PowerMockito.mock(PermissionService.class);
        PackageManager packageManager = PowerMockito.mock(PackageManager.class);
        BluetoothManager bluetoothManager = PowerMockito.mock(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = PowerMockito.mock(BluetoothAdapter.class);
        BluetoothLeScanner bluetoothLeScanner = PowerMockito.mock(BluetoothLeScanner.class);

        PowerMockito.when(permissionService.hasRequiredPermissions()).thenReturn(true);
        PowerMockito.when(packageManager.hasSystemFeature(Mockito.anyString())).thenReturn(true);
        PowerMockito.when(activity.getSystemService(Mockito.anyString())).thenReturn(bluetoothManager);
        PowerMockito.when(bluetoothManager.getAdapter()).thenReturn(bluetoothAdapter);
        PowerMockito.when(bluetoothAdapter.isEnabled()).thenReturn(true);
        PowerMockito.when(bluetoothAdapter.getBluetoothLeScanner()).thenReturn(bluetoothLeScanner);

        BleScannerService bleScannerService = new BleScannerService(activity, toastPrinter, permissionService, packageManager, bleGattCallback);
        bleScannerService.startBleScanner();
        Mockito.verify(bluetoothLeScanner, Mockito.times(1)).startScan(Mockito.anyList(), Mockito.any(ScanSettings.class), Mockito.any(ScanCallback.class));
    }

    @Test
    public void startBleScannerBleDisabled() {
        Activity activity = PowerMockito.mock(MainActivity.class);
        ToastPrinter toastPrinter = PowerMockito.mock(ToastPrinter.class);
        BleGattCallback bleGattCallback = PowerMockito.mock(BleGattCallback.class);
        PermissionService permissionService = PowerMockito.mock(PermissionService.class);
        PackageManager packageManager = PowerMockito.mock(PackageManager.class);
        BluetoothManager bluetoothManager = PowerMockito.mock(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = PowerMockito.mock(BluetoothAdapter.class);
        BluetoothLeScanner bluetoothLeScanner = PowerMockito.mock(BluetoothLeScanner.class);

        PowerMockito.when(permissionService.hasRequiredPermissions()).thenReturn(true);
        PowerMockito.when(packageManager.hasSystemFeature(Mockito.anyString())).thenReturn(true);
        PowerMockito.when(activity.getSystemService(Mockito.anyString())).thenReturn(bluetoothManager);
        PowerMockito.when(bluetoothManager.getAdapter()).thenReturn(bluetoothAdapter);
        PowerMockito.when(bluetoothAdapter.isEnabled()).thenReturn(false);

        BleScannerService bleScannerService = new BleScannerService(activity, toastPrinter, permissionService, packageManager, bleGattCallback);
        bleScannerService.startBleScanner();
        Mockito.verify(activity, Mockito.times(1)).startActivityForResult(Mockito.any(Intent.class), Mockito.anyInt());
    }

    @Test
    public void startBleScannerNoBle() {
        Activity activity = PowerMockito.mock(MainActivity.class);
        ToastPrinter toastPrinter = PowerMockito.mock(ToastPrinter.class);
        BleGattCallback bleGattCallback = PowerMockito.mock(BleGattCallback.class);
        PermissionService permissionService = PowerMockito.mock(PermissionService.class);
        PackageManager packageManager = PowerMockito.mock(PackageManager.class);

        PowerMockito.when(permissionService.hasRequiredPermissions()).thenReturn(true);
        PowerMockito.when(packageManager.hasSystemFeature(Mockito.anyString())).thenReturn(false);

        BleScannerService bleScannerService = new BleScannerService(activity, toastPrinter, permissionService, packageManager, bleGattCallback);
        bleScannerService.startBleScanner();
        Mockito.verify(activity, Mockito.times(0)).startActivityForResult(Mockito.any(Intent.class), Mockito.anyInt());
    }

    @Test
    public void startBleScannerNoPermissions() {
        Activity activity = PowerMockito.mock(MainActivity.class);
        ToastPrinter toastPrinter = PowerMockito.mock(ToastPrinter.class);
        BleGattCallback bleGattCallback = PowerMockito.mock(BleGattCallback.class);
        PermissionService permissionService = PowerMockito.mock(PermissionService.class);
        PackageManager packageManager = PowerMockito.mock(PackageManager.class);

        PowerMockito.when(permissionService.hasRequiredPermissions()).thenReturn(false);

        BleScannerService bleScannerService = new BleScannerService(activity, toastPrinter, permissionService, packageManager, bleGattCallback);
        bleScannerService.startBleScanner();
        Mockito.verify(activity, Mockito.times(0)).startActivityForResult(Mockito.any(Intent.class), Mockito.anyInt());
    }

    @Test
    public void getScanCallback() {
        Activity activity = PowerMockito.mock(MainActivity.class);
        ToastPrinter toastPrinter = PowerMockito.mock(ToastPrinter.class);
        BleGattCallback bleGattCallback = PowerMockito.mock(BleGattCallback.class);
        PermissionService permissionService = PowerMockito.mock(PermissionService.class);
        PackageManager packageManager = PowerMockito.mock(PackageManager.class);
        BluetoothManager bluetoothManager = PowerMockito.mock(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = PowerMockito.mock(BluetoothAdapter.class);
        BluetoothLeScanner bluetoothLeScanner = PowerMockito.mock(BluetoothLeScanner.class);
        ScanResult scanResult = PowerMockito.mock(ScanResult.class);
        BluetoothDevice bluetoothDevice = PowerMockito.mock(BluetoothDevice.class);
        BluetoothGatt deviceGatt = PowerMockito.mock(BluetoothGatt.class);

        PowerMockito.when(activity.getSystemService(Mockito.anyString())).thenReturn(bluetoothManager);
        PowerMockito.when(bluetoothManager.getAdapter()).thenReturn(bluetoothAdapter);
        PowerMockito.when(bluetoothAdapter.getBluetoothLeScanner()).thenReturn(bluetoothLeScanner);
        PowerMockito.when(bleGattCallback.isConnected()).thenReturn(false);
        PowerMockito.when(scanResult.getDevice()).thenReturn(bluetoothDevice);
        PowerMockito.when(bluetoothDevice.getAddress()).thenReturn("Test");
        PowerMockito.when(bluetoothDevice.connectGatt(Mockito.any(Activity.class), Mockito.anyBoolean(), Mockito.any(BleGattCallback.class), Mockito.anyInt())).thenReturn(deviceGatt);
        PowerMockito.when(activity.getString(Mockito.anyInt())).thenReturn("Test");

        BleScannerService bleScannerService = new BleScannerService(activity, toastPrinter, permissionService, packageManager, bleGattCallback);
        ScanCallback scanCallback = bleScannerService.getScanCallback();
        assertNotNull(scanCallback);

        scanCallback.onScanResult(0, scanResult);

        assertNotNull(bleScannerService.getDeviceGatt());

    }
}