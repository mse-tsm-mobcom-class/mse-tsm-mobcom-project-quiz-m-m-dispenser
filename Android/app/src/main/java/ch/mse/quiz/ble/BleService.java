package ch.mse.quiz.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class BleService {

    private static final long SCAN_PERIOD_MS = 10000;

    private final BluetoothLeScanner scanner;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<BluetoothDevice> foundDevices = new ArrayList<>();
    private final boolean scanDone = false;
    private final List<ScanFilter> filters = new ArrayList<>();
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(TAG, "onScanResult, result = " + result.getDevice().getAddress());
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d(TAG, "onScanFailed, errorCode = " + errorCode);
        }
    };

    public BleService(BluetoothLeScanner scanner) {
        this.scanner = scanner;
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.fromString("113A0001-FD33-441B-9A57-E9F1C29633D3"))).build();
        this.filters.add(filter);
    }

    public void scan(ScanCallback finalScanCallback) {
        assert (handler != null);
        assert (scanner != null);
        handler.postDelayed(() -> {
            Log.d(TAG, "stop scan");
            scanner.stopScan(finalScanCallback);
        }, SCAN_PERIOD_MS);
        Log.d(TAG, "start scan");
        scanner.startScan(this.filters, new ScanSettings.Builder().build(), finalScanCallback);
    }
}
