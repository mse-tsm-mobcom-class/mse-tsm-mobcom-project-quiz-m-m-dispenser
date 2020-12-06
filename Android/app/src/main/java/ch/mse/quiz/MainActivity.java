// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz.firebase.FirebaseLogin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.ble.BleService;
import ch.mse.quiz.listeners.FirebaseTopicListener;
import ch.mse.quiz.permission.PermissionService;
import ch.mse.quiz.printes.ToastPrinter;
import ch.mse.quiz.runnables.TheftRunnable;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements ToastPrinter {
    public static final String QUESTION_NUMBER = "ch.mse.quiz.extra.NUMBER";
    public static final String QUESTION_TOPIC = "ch.mse.quiz.extra.TOPIC";
    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();
    private Button btnStartQuizButton;
    private NumberPicker npNumberOfQuestions;
    public NumberPicker npTopic;
    private BluetoothDevice device;
    private BluetoothGatt deviceGatt;
    private static final int REQUEST_ENABLE_BT = 1;
    private final PermissionService permissionService = new PermissionService();
    private final BleGattCallback bleGattCallback = BleGattCallback.getInstance();

    //Firebase
    private FirebaseAuth mAuth;
    static int LAUNCH_SECOND_ACTIVITY = 11;
    public static final String USER_AUTH = "user_auth";
    //Getting Firebase Instance
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;

    private Thread theftThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        npTopic = findViewById(R.id.npQuestionTopic);
        // init firebase auth and get user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // if user is not logged in then redirect to login page
        if (currentUser == null) {
            Intent i = new Intent(this, FirebaseLogin.class);
            startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
        } else {
            print(getString(R.string.toastLoggedIn) + currentUser.getEmail());
        }
        //Getting Reference to Root Node
        dbRef = database.getReference("topics");
        //firebase init finished
        getTopics();

        initPermissions();

        initQuiz();
        checkTheft();

        Log.d(LOG_TAG, "-----");
        Log.d(LOG_TAG, "on create");
    }

    //OnActivityResult for LoginScreen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                print(getString(R.string.toastLoggedIn) + result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                print(getString(R.string.toastAuthFailed));
            }
        }
    }//onActivityResult

    private void initPermissions() {
        this.permissionService.checkPermissions(MainActivity.this);
        this.startBleScanner();
    }

    private void initQuiz() {
        btnStartQuizButton = findViewById(R.id.button_startQuiz);
        npNumberOfQuestions = findViewById(R.id.npQuestionNumber);
        Log.d(LOG_TAG, "start Quiz!");
        //how many questions to answer?
        npNumberOfQuestions.setMinValue(1);
        npNumberOfQuestions.setMaxValue(7);
        npNumberOfQuestions.setWrapSelectorWheel(false);

        btnStartQuizButton.setOnClickListener(v -> {
            if (!bleGattCallback.isConnected()) {
                print(getString(R.string.toastNoDispenserConnected));
                return;
            }
            Log.d(LOG_TAG, "start Quiz!");

            //if(this.bleGattCallback.isConnected()) {
            //yes? start Quiz Intent
            int choice = npTopic.getValue();
            String[] displayedValues = npTopic.getDisplayedValues();

            String topic = displayedValues[choice - 1];
            Bundle extras = new Bundle();
            extras.putInt(QUESTION_NUMBER, npNumberOfQuestions.getValue());
            extras.putString(QUESTION_TOPIC, topic);

            Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
            intent.putExtras(extras);

            startActivity(intent);
        });
    }

    private void checkTheft() {
        Runnable theftRunnable = new TheftRunnable(this, bleGattCallback);
        theftThread = new Thread(() -> {
            while (!theftThread.isInterrupted()) {
                try {
                    Thread.sleep(100);
                    runOnUiThread(theftRunnable);
                } catch (InterruptedException e) {
                    theftThread.interrupt();
                }
            }
        });
    }

    public void getTopics() {

        ArrayList<String> topics = new ArrayList<String>();

        dbRef.addListenerForSingleValueEvent(new FirebaseTopicListener(topics, npTopic));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
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
                BleService service = new BleService(bluetoothAdapter.getBluetoothLeScanner());
                service.scan(new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        if (null != result && !bleGattCallback.isConnected() && null == device) {
                            device = result.getDevice();
                            if (null != device) {
                                Log.i(LOG_TAG, device.getAddress());
                                deviceGatt = device.connectGatt(MainActivity.this, false, bleGattCallback,
                                        BluetoothDevice.TRANSPORT_AUTO);
                                print(getString(R.string.ble_connected));
                            }
                        }
                    }
                });
            } else {
                Log.d(TAG, "BLE not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Log.d(LOG_TAG, "BLE not available");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != deviceGatt) {
            deviceGatt.disconnect();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startBleScanner();
    }

    @Override
    public void print(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void printError(String message) {
        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
        TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
        toastMessage.setTextColor(Color.RED);
        toast.show();
    }

    @Override
    public void printError(int id) {
        printError(getString(id));
    }
}