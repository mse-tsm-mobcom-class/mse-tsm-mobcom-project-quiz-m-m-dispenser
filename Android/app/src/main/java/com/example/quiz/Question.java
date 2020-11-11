package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class Question extends AppCompatActivity {
    private static final String LOG_TAG = Question.class.getSimpleName();

    private int correctAnswer;
    private int currentQuestion;
    private int score;
    private int questionNumber;

    private ProgressBar pgTimer;
    private TextView tvProgress;
    private TextView tvQuestion;

    private TextView buttonAnswerA;
    private TextView buttonAnswerB;
    private TextView buttonAnswerC;
    private TextView buttonAnswerD;

    private KonfettiView konfettiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        pgTimer = findViewById(R.id.quiz_progressBar);
        tvProgress = findViewById(R.id.textView_questionProgress);
        konfettiView = findViewById(R.id.viewKonfetti);

        tvQuestion = findViewById(R.id.textView_questionText);
        buttonAnswerA = findViewById(R.id.textView_answerA);
        buttonAnswerB = findViewById(R.id.textView_answerB);
        buttonAnswerC = findViewById(R.id.textView_answerC);
        buttonAnswerD = findViewById(R.id.textView_answerD);

        //how many questions to do? get data from MainActivity calling
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        questionNumber = extras.getInt(MainActivity.QUESTION_NUMBER, 5);
        tvProgress.setText("Question " + currentQuestion + " out of " + questionNumber);

        //Button Listeners
        buttonAnswerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(0);
                Log.d(LOG_TAG, "checkAnswerA");
            }
        });

        buttonAnswerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(1);
                Log.d(LOG_TAG, "checkAnswerB");
            }
        });

        buttonAnswerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(2);
                Log.d(LOG_TAG, "checkAnswerC");
            }
        });

        buttonAnswerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(3);
                Log.d(LOG_TAG, "checkAnswerD");
            }
        });

        //create first question
        createQuestion();

        Log.d(LOG_TAG, "-----");
        Log.d(LOG_TAG, "on create");
    }


    private void createQuestion() {
        //TODO: get Question from FirebaseDB and set data accordingly
        tvQuestion.setText("Question");

        buttonAnswerA.setText("Option A");
        buttonAnswerB.setText("Option B");
        buttonAnswerC.setText("Option C");
        buttonAnswerD.setText("Option D");

        correctAnswer = 1;

        Log.d(LOG_TAG, "new question created!");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    private void submit(int answer) {
        Log.d(LOG_TAG, "checking if answer correct");

        //correct answer, fireworks!
        if (answer == correctAnswer) {
            score = score + 1;
            currentQuestion = currentQuestion + 1;

            konfettiView.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                    .addSizes(new Size(12, 5f))
                    .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                    .streamFor(300, 5000L);

        } else {
            Toast.makeText(getBaseContext(), "Better luck next time!", Toast.LENGTH_SHORT).show();
        }

        //set button backgrounds accordingly
        for (int i = 0; i <= 3; i++) {
            if (i == correctAnswer) {
                switch (i) {
                    case 0:
                        buttonAnswerA.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case 1:
                        buttonAnswerB.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case 2:
                        buttonAnswerC.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case 3:
                        buttonAnswerD.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                }
            } else {
                switch (i) {
                    case 0:
                        buttonAnswerA.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                    case 1:
                        buttonAnswerB.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                    case 2:
                        buttonAnswerC.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                    case 3:
                        buttonAnswerD.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                }
            }
        }
    }

    // when game is over, display result
    private void displayResult() {
        Log.d(LOG_TAG, "display QuizResultActivity!");
        Intent intent = new Intent(this, QuizResultActivity.class);
        startActivity(intent);
    }

}