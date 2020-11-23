package ch.mse.quiz.ble;

import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class BleService {

    private static final long SCAN_PERIOD_MS = 10000;

    private final String mmDispenserServiceUuid = "113A0001-FD33-441B-9A57-E9F1C29633D3";

    private final BluetoothLeScanner scanner;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<ScanFilter> filters = new ArrayList<>();

    public BleService(BluetoothLeScanner scanner) {
        this.scanner = scanner;
        ParcelUuid parcelUuid = ParcelUuid.fromString(mmDispenserServiceUuid);
        ScanFilter filter = new ScanFilter.Builder().setServiceUuid(parcelUuid).build();
        this.filters.add(filter);
    }

    public void scan(ScanCallback scanCallback) {
        assert (scanner != null);
        handler.postDelayed(() -> {
            Log.d(TAG, "stop scan");
            scanner.stopScan(scanCallback);
        }, SCAN_PERIOD_MS);
        Log.d(TAG, "start scan");
        scanner.startScan(this.filters, new ScanSettings.Builder().build(), scanCallback);
    }
}
