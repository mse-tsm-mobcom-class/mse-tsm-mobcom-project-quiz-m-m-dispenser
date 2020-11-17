package ch.mse.quiz;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ch.mse.quiz.ble.BleGattCallback;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class QuestionActivity extends AppCompatActivity {
    public static final String QUESTION_NUMBER = "ch.mse.quiz.extra.NUMBER";
    public static final String SCORE = "ch.mse.quiz.extra.SCORE";
    private static final String LOG_TAG = QuestionActivity.class.getSimpleName();
    private CountDownTimer countDownTimer;
    public int counter;
    private int correctAnswer;
    private int currentQuestion;
    private int questionNumber;
    private String questionTopic;
    private int score;

    private Handler handler = new Handler();

    public List<question> questions;

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
    private final BleGattCallback bleGattCallback = new BleGattCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        tvTimer = findViewById(R.id.quiz_Timer);
        tvProgress = findViewById(R.id.textView_questionProgress);
        tvDispenserState = findViewById(R.id.textView_dispenserState);
        konfettiView = findViewById(R.id.viewKonfetti);
        tvQuestion = findViewById(R.id.textView_questionText);
        buttonAnswerA = findViewById(R.id.textView_answerA);
        buttonAnswerB = findViewById(R.id.textView_answerB);
        buttonAnswerC = findViewById(R.id.textView_answerC);
        buttonAnswerD = findViewById(R.id.textView_answerD);

        //how many questions to do? get data from MainActivity calling
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        questionNumber = extras.getInt(MainActivity.QUESTION_NUMBER);
        questionTopic = extras.getString(MainActivity.QUESTION_TOPIC);
        tvProgress.setText("Topic " + questionTopic + " Question " + currentQuestion + " out of " + questionNumber);

        //read required amount of questions from DB
        questions = getQuestions();
        //set initial values
        currentQuestion = 1;
        score = 0;

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

        Log.d(LOG_TAG, "-----");
        Log.d(LOG_TAG, "on create");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart");

        //set first question UI
        tvDispenserState.setText("right answer, get M&M");
        resetButtonColor();
        createQuestion(currentQuestion-1);
        startTimer();
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

    private Runnable newQuestion = new Runnable() {
        @Override
        public void run() {
            createQuestion(currentQuestion);
            tvProgress.setText("Topic " + questionTopic + " Question " + currentQuestion + " out of " + questionNumber);
            resetButtonColor();
            startTimer();
        }
    };

    private Runnable showResultActivity = new Runnable() {
        @Override
        public void run() {
            Log.d(LOG_TAG, "display QuizResultActivity!");
            Bundle extras = new Bundle();
            extras.putInt(QUESTION_NUMBER, questionNumber);
            extras.putInt(SCORE, score);
            Intent intent = new Intent(QuestionActivity.this, QuizResultActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        }
    };

    //timer
    private void startTimer() {
        Log.d(LOG_TAG, "Time is running!");
        counter = 30;

        countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                counter=counter-1;
                tvTimer.setText(String.valueOf(counter));
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
        if(currentQuestion <= questionNumber) {
            handler.postDelayed(newQuestion, 3000);
        } else {
            //otherwise result page
            handler.postDelayed(showResultActivity, 3000);
        }
    }

    //Questions: read from DB
    //TODO: read random questions from FirebaseDB
    private List<question> getQuestions() {
        Log.d(LOG_TAG, "new question list created!");

        List<question> questions = new ArrayList<>();

        String question = "What is the capital of France?";
        String[] answers = {"Germany", "Switzerland", "France", "USA"};
        for (int i = 0; i <= questionNumber; i++) {
            questions.add(new question(question, 2, answers));
        }

        return questions;
    }

    //create new question from list and set UI accordingly
    private void createQuestion(int i) {
        Log.d(LOG_TAG, "new question created!");
        question q = questions.get(i);
        tvQuestion.setText(q.getQuestion());

        buttonAnswerA.setText(q.getAnswer(0));
        buttonAnswerB.setText(q.getAnswer(1));
        buttonAnswerC.setText(q.getAnswer(2));
        buttonAnswerD.setText(q.getAnswer(3));

        correctAnswer = q.getCorrectAnswer();
    }

    //UI changers
    private void resetButtonColor() {
        buttonAnswerA.setBackgroundColor(getResources().getColor(R.color.grey));
        buttonAnswerB.setBackgroundColor(getResources().getColor(R.color.grey));
        buttonAnswerC.setBackgroundColor(getResources().getColor(R.color.grey));
        buttonAnswerD.setBackgroundColor(getResources().getColor(R.color.grey));
    }

    private void setButtonColor() {
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

    //BLE
    private void  dispenseCandy() {
        bleGattCallback.dispense();
        if(bleGattCallback.isDispenserState())
            tvDispenserState.setText("grab your m&m");
        else
            tvDispenserState.setText("nothing to take");
    }

    //Quiz handlers
    //check for correct answer and change UI
    private void submit(int answer) {
        Log.d(LOG_TAG, "checking if answer correct");
        currentQuestion = currentQuestion + 1;

        //correct answer, fireworks!
        if (answer == correctAnswer) {
            score = score + 1;

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
            dispenseCandy();

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
            handler.postDelayed(showResultActivity,3000);
        }
    }

}