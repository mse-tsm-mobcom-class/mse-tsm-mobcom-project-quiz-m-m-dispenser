// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ch.mse.quiz.models.userScore;

import static android.content.ContentValues.TAG;

public class QuizResultActivity extends AppCompatActivity {
    private static final String LOG_TAG = QuizResultActivity.class.getSimpleName();
    private TextView results;
    private ListView leaderboard;
    private Button restartQuiz;
    private Button endQuiz;
    //scores this round
    private int userScore;
    private int totalQuestions;
    private String quizTopic;
    //leaderboard user scores
    public ArrayList<userScore> playerScores;
    public userScore currentPlayer;

    //firebase
    //Getting Firebase Instance
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef;
    //auth of user
    private FirebaseAuth mAuth;
    public static final String USER_AUTH = "user_auth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //get results from this quiz round
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        userScore = extras.getInt(QuestionActivity.SCORE);
        totalQuestions = extras.getInt(QuestionActivity.QUESTION_NUMBER);
        quizTopic = extras.getString(QuestionActivity.QUIZ_TOPIC);

        // init firebase auth and get user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //set current user values
        currentPlayer = new userScore();
        currentPlayer.setUserName(currentUser.getEmail());
        currentPlayer.setUserScore(userScore);

        //update & read leaderboard
        playerScores = new ArrayList<>();
        updateUserScores(currentPlayer);

        results = findViewById(R.id.textView_results);
        results.setText("Your score is " + userScore + " out of " + totalQuestions + " questions");

        restartQuiz = findViewById(R.id.button_restartQuiz);
        restartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        endQuiz = findViewById(R.id.button_endQuiz);
        endQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    private void updateUserScores(userScore currentPlayer) {
        //Getting Reference to highscore table
        dbRef = database.getReference("Leaders_" + quizTopic);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("dbtag", "ondatachangecalled");
                //read all users for that topic
                Iterable<DataSnapshot> children = snapshot.getChildren();
                children.forEach(i -> {
                    playerScores.add(i.getValue(userScore.class));
                });
                playerScores.add(currentPlayer);

                updateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: Something went wrong! Error:" + error.getMessage());
            }
        });

    }

    private void updateList() {
        bubbleSort(playerScores);
        //write to DB
        dbRef = database.getReference("Leaders_" + quizTopic);
        dbRef.setValue(playerScores);

        //display top 5
        String highscoreArray[];
        if (playerScores.size() < 5) {
            highscoreArray = new String[playerScores.size()];
            for (int i = 0; i < playerScores.size(); i++) {
                highscoreArray[i] = playerScores.get(i).getUserName() + " Score " + playerScores.get(i).getUserScore();
            }
        } else {
            highscoreArray = new String[5];
            for (int i = 0; i < 5; i++) {
                highscoreArray[i] = playerScores.get(i).getUserName() + " Score " + playerScores.get(i).getUserScore();
            }
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, highscoreArray);
        leaderboard = findViewById(R.id.listView_highScore);
        leaderboard.setAdapter(adapter);
    }

    private void bubbleSort(ArrayList<userScore> userScores) {
        int n = userScores.size();
        userScore tempUser = new userScore();

        for (int i = n; i > 1; i--) {
            for (int j = i - 1; j > 0; j--) {
                //bubble up if score greater
                if (userScores.get(j - 1).getUserScore() < userScores.get(j).getUserScore()) {
                    tempUser = userScores.get(j - 1);
                    userScores.set(j - 1, userScores.get(j));
                    userScores.set(j, tempUser);
                }

            }
        }
    }
}