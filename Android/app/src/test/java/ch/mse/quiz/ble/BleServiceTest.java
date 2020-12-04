// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
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

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class BleServiceTest extends TestCase {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private BluetoothLeScanner scanner;
    @Mock
    private BluetoothDevice device;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Mockito.when(device.getName()).thenReturn("test device");
    }

    @Test
    public void testInitBleScanner() {
        BleService bleService = new BleService(scanner);
        assertNotNull("Ble Service not initialized", bleService);
    }

    @Test
    public void testScan() {
        BleService bleService = new BleService(scanner);
        bleService.scan(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
            }
        });
        assertTrue("scan not worked", true);
    }
}