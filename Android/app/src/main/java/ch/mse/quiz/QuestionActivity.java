// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.listeners.FirebaseQuestionListener;
import ch.mse.quiz.listeners.StartQuizListener;
import ch.mse.quiz.models.question;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class QuestionActivity extends AppCompatActivity {
    public static final String QUESTION_NUMBER = "ch.mse.quiz.extra.NUMBER";
    public static final String SCORE = "ch.mse.quiz.extra.SCORE";
    public static final String QUIZ_TOPIC = "ch.mse.quiz.extra.QUIZ_TOPIC";
    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();
    private CountDownTimer countDownTimer;
    private long remainingTime;
    private int correctAnswer;
    private int currentQuestion;
    private int questionNumber;
    private String quizTopic;
    private int userScore;
    public ArrayList<question> questions = new ArrayList<question>();
    private final Handler handler = new Handler();

    private TextView tvTimer;
    private TextView tvProgress;
    private TextView tvQuestion;

    private TextView buttonAnswerA;
    private TextView buttonAnswerB;
    private TextView buttonAnswerC;
    private TextView buttonAnswerD;
    private TextView tvDispenserState;
    private KonfettiView konfettiView;

    //BLE
    private final BleGattCallback bleGattCallback = BleGattCallback.getInstance();


    private final Runnable newQuestion = new Runnable() {
        @Override
        public void run() {
            createQuestion(currentQuestion);
            tvProgress.setText("Topic " + quizTopic + " Question " + currentQuestion + " out of " + questionNumber);
            resetButtonColor();
            startTimer(30000);
        }
    };

    private final Runnable showResultActivity = new Runnable() {
        @Override
        public void run() {
            Log.d(LOG_TAG, "display QuizResultActivity!");
            Bundle extras = new Bundle();
            extras.putInt(QUESTION_NUMBER, questionNumber);
            extras.putInt(SCORE, userScore);
            extras.putString(QUIZ_TOPIC, quizTopic);
            Intent intent = new Intent(QuestionActivity.this, QuizResultActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
            finish();
        }
    };
    private TextView tvFillingLevel;
    private Thread fillingLevelThread;
    private Thread dispenseStatusThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        konfettiView = findViewById(R.id.viewKonfetti);
        tvProgress = findViewById(R.id.textView_questionProgress);
        tvTimer = findViewById(R.id.quiz_Timer);
        tvDispenserState = findViewById(R.id.textView_dispenserState);
        tvFillingLevel = findViewById(R.id.tvFillingLevel);
        tvQuestion = findViewById(R.id.textView_questionText);
        buttonAnswerA = findViewById(R.id.textView_answerA);
        buttonAnswerB = findViewById(R.id.textView_answerB);
        buttonAnswerC = findViewById(R.id.textView_answerC);
        buttonAnswerD = findViewById(R.id.textView_answerD);

        //set initial values
        currentQuestion = 1;
        userScore = 0;
        remainingTime = 30000;

        //how many questions to do? get data from MainActivity calling
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        questionNumber = extras.getInt(StartQuizListener.QUESTION_NUMBER);
        quizTopic = extras.getString(StartQuizListener.QUESTION_TOPIC);

        //read required amount of questions from DB
        getQuestions();

        //filling level from sensor
        initFillingLevelThread();
        dispenseCandy();

        //Button Listeners
        buttonAnswerA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(1);
                Log.d(LOG_TAG, "checkAnswerA");
            }
        });

        buttonAnswerB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(2);
                Log.d(LOG_TAG, "checkAnswerB");
            }
        });

        buttonAnswerC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(3);
                Log.d(LOG_TAG, "checkAnswerC");
            }
        });

        buttonAnswerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(4);
                Log.d(LOG_TAG, "checkAnswerD");
            }
        });

        Log.d(LOG_TAG, "-----");
        Log.d(LOG_TAG, "on create");
    }
    //firebase
    //Getting Firebase Instance
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;
    //TODO: read random questions from FirebaseDB
    public void getQuestions() {
        dbRef = database.getReference("topics/" + quizTopic + "/questions");

        dbRef.addListenerForSingleValueEvent(new FirebaseQuestionListener(questions,questionNumber,this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");
        //set first question UI
        tvDispenserState.setText("");
        if (!fillingLevelThread.isAlive()) {
            fillingLevelThread.start();
        }
        if (!dispenseStatusThread.isAlive()) {
            dispenseStatusThread.start();
        }
        resetButtonColor();
        startTimer(remainingTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        //stopTimer
        countDownTimer.cancel();
        countDownTimer = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        //restart Timer
        if (countDownTimer == null) {
            startTimer(remainingTime);
        }
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

    //BLE
    private void dispenseCandy() {
        dispenseStatusThread = new Thread(() -> {
            while (!dispenseStatusThread.isInterrupted()) {
                try {
                    Thread.sleep(100);
                    runOnUiThread(() -> {
                        String dispenseStatus = "";
                        if (bleGattCallback.isDispenserState()) {
                            dispenseStatus = "grab your m&m";
                        } else {
                            dispenseStatus = "nothing to take";
                        }
                        tvDispenserState.setText(dispenseStatus);
                    });
                } catch (InterruptedException e) {
                    dispenseStatusThread.interrupt();
                }
            }
        });
    }

    public void initFillingLevelThread() {
        fillingLevelThread = new Thread(() -> {
            while (!fillingLevelThread.isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(() -> tvFillingLevel.setText(String.format(Locale.GERMAN, "%s %d%%", getResources().getString(R.string.tv_filling_level), Math.round(bleGattCallback.getFillingLevelPercentage()))));
                } catch (InterruptedException e) {
                    fillingLevelThread.interrupt();
                }
                Log.d(LOG_TAG, "onResume");
            }
        });
    }

    //create new question from list and set UI accordingly
    public void createQuestion(int i) {
        Log.d(LOG_TAG, "new question created!");
        tvProgress.setText("Topic " + quizTopic + " Question " + currentQuestion + " out of " + questionNumber);
        question q = questions.get(i - 1);
        tvQuestion.setText(q.getQuestion());

        buttonAnswerA.setText(q.getAnswer1());
        buttonAnswerB.setText(q.getAnswer2());
        buttonAnswerC.setText(q.getAnswer3());
        buttonAnswerD.setText(q.getAnswer4());

        correctAnswer = q.getCorrectAnswer();
    }

    //timer
    private void startTimer(long runtime) {
        Log.d(LOG_TAG, "Time is running!");

        countDownTimer = new CountDownTimer(runtime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.valueOf(millisUntilFinished / 1000L));
                remainingTime = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                tvTimer.setText("time is up!");
                endQuestion();
            }
        }.start();
    }

    //end timer
    private void endTimer() {
        countDownTimer.cancel();
    }

    //user out of time
    private void endQuestion() {
        Log.d(LOG_TAG, "User out of time, end question!");

        currentQuestion = currentQuestion + 1;
        Toast.makeText(getBaseContext(), "Time is up!", Toast.LENGTH_SHORT).show();
        setButtonColor();

        //more questions? then create new one
        if (currentQuestion <= questionNumber) {
            handler.postDelayed(newQuestion, 3000);
        } else {
            //otherwise result page
            handler.postDelayed(showResultActivity, 3000);
        }
    }

    //UI changers
    private void resetButtonColor() {
        buttonAnswerA.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
        buttonAnswerB.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
        buttonAnswerC.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
        buttonAnswerD.setBackgroundColor(getResources().getColor(R.color.secondaryColor));
    }

    private void setButtonColor() {
        for (int i = 1; i <= 4; i++) {
            if (i == correctAnswer) {
                switch (i) {
                    case 1:
                        buttonAnswerA.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case 2:
                        buttonAnswerB.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case 3:
                        buttonAnswerC.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                    case 4:
                        buttonAnswerD.setBackgroundColor(getResources().getColor(R.color.green));
                        break;
                }
            } else {
                switch (i) {
                    case 1:
                        buttonAnswerA.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                    case 2:
                        buttonAnswerB.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                    case 3:
                        buttonAnswerC.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                    case 4:
                        buttonAnswerD.setBackgroundColor(getResources().getColor(R.color.red));
                        break;
                }
            }
        }
    }

    //Quiz handlers
    //check for correct answer and change UI
    private void submit(int answer) {
        Log.d(LOG_TAG, "checking if answer correct");
        currentQuestion = currentQuestion + 1;

        //correct answer, fireworks!
        if (answer == correctAnswer) {
            userScore = userScore + 1;

            //visuals
            konfettiView.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0, 359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(2000L)
                    .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                    .addSizes(new Size(12, 5f))
                    .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                    .streamFor(300, 3000L);

            //activate M&M dispenser via BLE
            bleGattCallback.dispense();

        } else {
            Toast.makeText(getBaseContext(), "Better luck next time!", Toast.LENGTH_SHORT).show();
        }

        //set button backgrounds: correct answer=green, wrong answer=red
        setButtonColor();

        //more questions to answer? create new question, otherwise display quiz results
        if (currentQuestion <= questionNumber) {
            endTimer();
            handler.postDelayed(newQuestion, 3000);
        } else {
            endTimer();
            handler.postDelayed(showResultActivity, 3000);
        }
    }

}