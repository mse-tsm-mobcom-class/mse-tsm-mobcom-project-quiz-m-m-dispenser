package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String QUESTION_NUMBER = "com.example.quiz.extra.MESSAGE";
    private static final String LOG_TAG = Question.class.getSimpleName();
    private Button btnStartQuizButton;
    private NumberPicker npNumberOfQuestions;
    private int QuestionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartQuizButton = findViewById(R.id.button_startQuiz);
        npNumberOfQuestions = findViewById(R.id.npQuestionNumber);

        //how many questions to answer?
        npNumberOfQuestions.setMinValue(0);
        npNumberOfQuestions.setMaxValue(7);
        npNumberOfQuestions.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //tvNumberOfQuestions.setText("number of questions : " + newVal);
                Log.d(LOG_TAG, "question count selected");
            }
        });

        btnStartQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "start Quiz!");
                //TODO: Bluetooth connection setup

                //start Quiz Intent
                Bundle extras = new Bundle();
                extras.putInt(QUESTION_NUMBER, npNumberOfQuestions.getValue());
                Intent intent = new Intent(MainActivity.this, Question.class);

                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        Log.d(LOG_TAG, "-----");
        Log.d(LOG_TAG, "on create");
    }

}