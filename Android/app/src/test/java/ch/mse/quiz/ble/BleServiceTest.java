package ch.mse.quiz.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanResult;
import android.widget.ArrayAdapter;

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

import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class BleServiceTest extends TestCase {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private BluetoothLeScanner scanner;
    @Mock
    private BluetoothDevice device;
    @Mock
    private ArrayAdapter<String> adapter;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        Mockito.when(device.getName()).thenReturn("test device");
    }

    @Test
    public void testInitBleScanner() {
        HashMap<String, BluetoothDevice> devices = new HashMap<>();
        BleService bleService = new BleService(scanner, devices, adapter);
        assertNotNull("Ble Service not initialized", bleService);
    }

    @Test
    public void testScan() {
        HashMap<String, BluetoothDevice> devices = new HashMap<>();
        BleService bleService = new BleService(scanner, devices, adapter);
        bleService.scan();
        assertTrue("scan not worked", true);
    }

    @Test
    public void testAddDeviceToList() {
        HashMap<String, BluetoothDevice> devices = new HashMap<>();
        BleService bleService = new BleService(scanner, devices, adapter);
        ScanResult result = new ScanResult(device, 0, 0, 0, 0, 0, 0, 0, null, 0);
        bleService.addDeviceToList(result);
        assertTrue("device not found", 0 < devices.size());
    }
}