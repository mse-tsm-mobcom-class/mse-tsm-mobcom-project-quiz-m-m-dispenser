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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.models.question;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static android.content.ContentValues.TAG;

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
    //BLE
    private final BleGattCallback bleGattCallback = BleGattCallback.getInstance();
    private KonfettiView konfettiView;
    private final Runnable showResultActivity = new Runnable() {
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

    //firebase
    //Getting Firebase Instance
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;
    private final Runnable newQuestion = new Runnable() {
        @Override
        public void run() {
            createQuestion(currentQuestion);
            tvProgress.setText("Topic " + questionTopic + " Question " + currentQuestion + " out of " + questionNumber);
            resetButtonColor();
            startTimer();
        }
    };
    private TextView tvFillingLevel;
    private Thread fillingLevelThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        tvProgress = findViewById(R.id.textView_questionProgress);
        tvTimer = findViewById(R.id.quiz_Timer);

        //set initial values
        currentQuestion = 1;
        score = 0;
        //how many questions to do? get data from MainActivity calling
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        questionNumber = extras.getInt(MainActivity.QUESTION_NUMBER);
        questionTopic = extras.getString(MainActivity.QUESTION_TOPIC);

        //read required amount of questions from DB
        getQuestions();

        tvDispenserState = findViewById(R.id.textView_dispenserState);
        tvFillingLevel = findViewById(R.id.tvFillingLevel);

        initFillingLevelThread();
        //pbTimer = findViewById(R.id.quiz_progressBar);
        konfettiView = findViewById(R.id.viewKonfetti);
        tvQuestion = findViewById(R.id.textView_questionText);
        buttonAnswerA = findViewById(R.id.textView_answerA);
        buttonAnswerB = findViewById(R.id.textView_answerB);
        buttonAnswerC = findViewById(R.id.textView_answerC);
        buttonAnswerD = findViewById(R.id.textView_answerD);

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

    /*
        //timer
        private void startCountdownTimer() {
            int i = 0;
            pbTimer.setProgress(i);
            countDownTimer = new CountDownTimer(5000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    i++;
                    pbTimer.setProgress((int) i * 100/(5000/1000));
                }

                @Override
                public void onFinish() {
                    i++;
                    pbTimer.setProgress(100);
                }
            }
        }

        */
    //TODO: read random questions from FirebaseDB
    private void getQuestions() {
        dbRef = database.getReference("topics/"+ questionTopic + "/questions");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                //check if amount of question is h
                int questionnr = (int) dataSnapshot.getChildrenCount();

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                children.forEach(i -> {
                    questions.add(i.getValue(question.class));
                });
                // if less questions are in the DB then chosen by the user
                //TODO: discuss UX for this matter and possibly add Toast to inform user
                if (questionnr <= questionNumber) {
                    questionNumber = questionnr;
                    Toast.makeText(getBaseContext(), "Nr. of question adjusted. Only " + questionnr + " questions available.", Toast.LENGTH_SHORT).show();
                }
                //set first question UI
                createQuestion(currentQuestion);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage());

            }
        });
    }

    public void initFillingLevelThread() {
        fillingLevelThread = new Thread(() -> {
            while (!fillingLevelThread.isInterrupted()) {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(() -> tvFillingLevel.setText(String.format(Locale.GERMAN, "%s %.2f", getResources().getString(R.string.tv_filling_level), bleGattCallback.getFillingLevelPercentage())));
                } catch (InterruptedException e) {
                    fillingLevelThread.interrupt();
                }
            }
        });
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

    //create new question from list and set UI accordingly
    private void createQuestion(int i) {
        tvProgress.setText("Topic " + questionTopic + " Question " + currentQuestion + " out of " + questionNumber);
        question q = questions.get(i - 1);
        tvQuestion.setText(q.getQuestion());

        buttonAnswerA.setText(q.getAnswer1());
        buttonAnswerB.setText(q.getAnswer2());
        buttonAnswerC.setText(q.getAnswer3());
        buttonAnswerD.setText(q.getAnswer4());

        correctAnswer = q.getCorrectAnswer();

        Log.d(LOG_TAG, "new question created!");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(LOG_TAG, "onStart");

        //set first question UI
        tvDispenserState.setText("right answer, get M&M");
        fillingLevelThread.start();
        resetButtonColor();
        startTimer();
    }

    //timer
    private void startTimer() {
        Log.d(LOG_TAG, "Time is running!");
        counter = 30;

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                counter = counter - 1;
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
        if (currentQuestion <= questionNumber) {
            handler.postDelayed(newQuestion, 3000);
        } else {
            //otherwise result page
            handler.postDelayed(showResultActivity, 3000);
        }
    }

    //UI changers
    private void resetButtonColor() {
        buttonAnswerA.setBackgroundColor(getResources().getColor(R.color.light_blue));
        buttonAnswerB.setBackgroundColor(getResources().getColor(R.color.light_blue));
        buttonAnswerC.setBackgroundColor(getResources().getColor(R.color.light_blue));
        buttonAnswerD.setBackgroundColor(getResources().getColor(R.color.light_blue));
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

    //BLE
    private void dispenseCandy() {
        bleGattCallback.dispense();
        if (bleGattCallback.isDispenserState())
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
            handler.postDelayed(showResultActivity, 3000);
        }
    }

}