// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.mse.quiz.app.App;
import ch.mse.quiz.listeners.FirebaseScoreListener;
import ch.mse.quiz.listeners.StartQuizListener;
import ch.mse.quiz.models.UserScore;

public class QuizResultActivity extends AppCompatActivity {
    private String quizTopic;
    //firebase
    //Getting Firebase Instance
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //leaderboard user scores
    private List<UserScore> playerScores;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.activities.add(this);
        setContentView(R.layout.activity_quiz_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //get results from this quiz round
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //scores this round
        int userScore = extras.getInt(QuestionActivity.SCORE);
        int totalQuestions = extras.getInt(StartQuizListener.QUESTION_NUMBER);
        quizTopic = extras.getString(StartQuizListener.QUESTION_TOPIC);

        // init firebase auth and get user
        //auth of user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //set current user values
        UserScore currentPlayer = new UserScore();
        currentPlayer.setUserName(currentUser.getEmail());
        currentPlayer.setUserScore(userScore);

        //update & read leaderboard
        playerScores = new ArrayList<>();
        updateUserScores(currentPlayer);

        TextView results = findViewById(R.id.textView_results);
        results.setText(String.format(Locale.GERMAN, "Your score is %d out of %d questions", userScore, totalQuestions));

        Button restartQuiz = findViewById(R.id.button_restartQuiz);
        restartQuiz.setOnClickListener(v -> {
            Intent intent1 = new Intent(QuizResultActivity.this, MainActivity.class);
            startActivity(intent1);
        });

        Button endQuiz = findViewById(R.id.button_endQuiz);
        endQuiz.setOnClickListener(v -> App.finish());
    }

    private void updateUserScores(UserScore currentPlayer) {
        //Getting Reference to highscore table
        dbRef = database.getReference("Leaders_" + quizTopic);

        dbRef.addListenerForSingleValueEvent(new FirebaseScoreListener(playerScores, currentPlayer, this));

    }

    public void updateList() {
        playerScores.sort((o1, o2) -> o1.getUserScore() - o2.getUserScore());
        //write to DB
        dbRef = database.getReference("Leaders_" + quizTopic);
        dbRef.setValue(playerScores);

        //display top 5
        String[] highscoreArray;
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.activity_listview, highscoreArray);
        ListView leaderboard = findViewById(R.id.listView_highScore);
        leaderboard.setAdapter(adapter);
    }
}