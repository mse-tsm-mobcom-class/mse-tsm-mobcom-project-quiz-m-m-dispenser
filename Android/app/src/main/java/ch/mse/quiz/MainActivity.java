package ch.mse.quiz;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.ble.BleService;
import ch.mse.quiz.permission.PermissionService;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    public static final String QUESTION_NUMBER = "ch.mse.quiz.extra.MESSAGE";
    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();
    private Button btnStartQuizButton;
    private NumberPicker npNumberOfQuestions;
    private static final int REQUEST_ENABLE_BT = 1;
    private final PermissionService permissionService = new PermissionService();
    private final HashMap<String, BluetoothDevice> devices = new HashMap<>();
    private static final String SPINNER_DEFAULT_VALUE = "Select dispenser";
    private final BleGattCallback bleGattCallback = new BleGattCallback();
    private Spinner spBleScanResult;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBleDeviceSelection();

        initPermissions();

        Button btnDispense = findViewById(R.id.btnDispense);
        btnDispense.setOnClickListener(v -> bleGattCallback.dispense());

        initQuiz();

        Log.d(LOG_TAG, "-----");
        Log.d(LOG_TAG, "on create");
    }

    private void initPermissions() {
        this.permissionService.checkPermissions(MainActivity.this);
        this.startBleScanner();
    }

    private void initQuiz() {
        btnStartQuizButton = findViewById(R.id.button_startQuiz);
        btnStartQuizButton.setEnabled(this.bleGattCallback.isConnected());


        npNumberOfQuestions = findViewById(R.id.npQuestionNumber);

        //how many questions to answer?
        npNumberOfQuestions.setMinValue(0);
        npNumberOfQuestions.setMaxValue(7);

        btnStartQuizButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "start Quiz!");

            //start Quiz Intent
            Bundle extras = new Bundle();
            extras.putInt(QUESTION_NUMBER, npNumberOfQuestions.getValue());
            Intent intent = new Intent(MainActivity.this, QuestionActivity.class);

            intent.putExtras(extras);
            startActivity(intent);
        });
    }

    private void initBleDeviceSelection() {
        spBleScanResult = findViewById(R.id.spBleScanResult);
        adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapter.add(SPINNER_DEFAULT_VALUE);
        spBleScanResult.setAdapter(adapter);

        spBleScanResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                if (null != textView && !textView.getText().toString().equals(SPINNER_DEFAULT_VALUE)) {
                    BluetoothDevice device = devices.get(textView.getText().toString());
                    if (null != device) {
                        Log.i(LOG_TAG, device.getAddress());
                        device.connectGatt(MainActivity.this, false, bleGattCallback,
                                BluetoothDevice.TRANSPORT_AUTO);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i(LOG_TAG, "no device selected");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        btnStartQuizButton.setEnabled(this.permissionService.hasRequiredPermissions());
        this.startBleScanner();
    }

    private void startBleScanner() {
        if (!this.permissionService.hasRequiredPermissions()) {
            return;
        }
        boolean hasBle = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        // Or <uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
        if (hasBle) {
            Log.d(LOG_TAG, "BLE available");
            BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                Log.d(LOG_TAG, "BLE enabled");
                BleService service = new BleService(bluetoothAdapter.getBluetoothLeScanner(), this.devices, this.adapter);
                service.scan();
            } else {
                Log.d(TAG, "BLE not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Log.d(LOG_TAG, "BLE not available");
        }
    }
}