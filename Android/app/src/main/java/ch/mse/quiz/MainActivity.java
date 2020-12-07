// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

import android.app.Activity;
import android.content.Intent;
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

import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.ble.BleScannerService;
import ch.mse.quiz.listeners.FirebaseTopicListener;
import ch.mse.quiz.listeners.StartQuizListener;
import ch.mse.quiz.permission.PermissionService;
import ch.mse.quiz.printes.ToastPrinter;
import ch.mse.quiz.runnables.TheftRunnable;

public class MainActivity extends AppCompatActivity implements ToastPrinter {
    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();
    private Button btnStartQuizButton;
    private NumberPicker npNumberOfQuestions;
    public NumberPicker npTopic;
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

    private FirebaseTopicListener firebaseTopicListener;

    private BleScannerService bleScannerService;

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
        bleScannerService = new BleScannerService(this, this, permissionService, getPackageManager(), bleGattCallback);
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
        bleScannerService.startBleScanner();
    }

    private void initQuiz() {
        btnStartQuizButton = findViewById(R.id.button_startQuiz);
        npNumberOfQuestions = findViewById(R.id.npQuestionNumber);
        Log.d(LOG_TAG, "start Quiz!");
        //how many questions to answer?
        npNumberOfQuestions.setMinValue(1);
        npNumberOfQuestions.setMaxValue(7);
        npNumberOfQuestions.setWrapSelectorWheel(false);

        getTopics();

        btnStartQuizButton.setOnClickListener(new StartQuizListener(this, this, bleGattCallback, npTopic, npNumberOfQuestions, firebaseTopicListener.getTopics()));
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

        if (null == firebaseTopicListener) {
            firebaseTopicListener = new FirebaseTopicListener(npTopic);
        }

        dbRef.addListenerForSingleValueEvent(firebaseTopicListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        bleScannerService.startBleScanner();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != bleGattCallback && null != bleScannerService.getDeviceGatt()) {
            bleScannerService.getDeviceGatt().disconnect();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bleScannerService.startBleScanner();
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