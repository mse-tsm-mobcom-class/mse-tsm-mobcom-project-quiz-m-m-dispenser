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
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quiz.firebase.FirebaseLogin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.ble.BleService;
import ch.mse.quiz.permission.PermissionService;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    public static final String QUESTION_NUMBER = "ch.mse.quiz.extra.NUMBER";
    public static final String QUESTION_TOPIC = "ch.mse.quiz.extra.TOPIC";
    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();
    private Button btnStartQuizButton;
    private NumberPicker npNumberOfQuestions;
    private NumberPicker npTopic;
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
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;


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
            Toast.makeText(this, "Loged in as:" + currentUser.getEmail(),
                    Toast.LENGTH_LONG).show();
        }
        //Getting Reference to Root Node
        dbRef = database.getReference("topics");
        //firebase init finished

        initPermissions();

        Button btnDispense = findViewById(R.id.btnDispense);
        btnDispense.setOnClickListener(v -> bleGattCallback.dispense());
        initQuiz();

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
                Toast.makeText(this, "Loged in as: " + result,
                        Toast.LENGTH_LONG).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Authentication failed.",
                        Toast.LENGTH_LONG).show();
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

        ArrayList<String> topiclist = getTopics();

        btnStartQuizButton.setOnClickListener(v -> {
            Log.d(LOG_TAG, "start Quiz!");

            //are we connected to M&M dispenser?
            //if(this.bleGattCallback.isConnected()) {
            //yes? start Quiz Intent
            int choice = npTopic.getValue();
            String topic = topiclist.get(choice - 1);
            Bundle extras = new Bundle();
            extras.putInt(QUESTION_NUMBER, npNumberOfQuestions.getValue());
            extras.putString(QUESTION_TOPIC, topic);

            Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
            intent.putExtras(extras);

            startActivity(intent);
            //} else {
            //no? ask user to connect first
            // Toast.makeText(getBaseContext(), "Please connect to the M&M candy store!", Toast.LENGTH_SHORT).show();
            //}
        });
    }

    private ArrayList<String> getTopics() {

        ArrayList<String> topics = new ArrayList<String>();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Getting the string value of that node
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                children.forEach(i -> {
                    topics.add(i.getKey());
                });
                String[] topicSelection = topics.toArray(new String[topics.size()]);
                //which topic?
                npTopic.setMinValue(1);
                npTopic.setMaxValue(topics.size());
                npTopic.setDisplayedValues(topicSelection);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage());

            }
        });
        return topics;
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
                                Toast.makeText(MainActivity.this, R.string.ble_connected, Toast.LENGTH_LONG).show();
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
    protected void onStop() {
        super.onStop();
        if (null != deviceGatt) {
            deviceGatt.disconnect();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startBleScanner();
    }
}