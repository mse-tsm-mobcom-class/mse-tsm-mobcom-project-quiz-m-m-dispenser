package ch.mse.quiz.listeners;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import ch.mse.quiz.QuizResultActivity;
import ch.mse.quiz.models.userScore;

import static android.content.ContentValues.TAG;

public class FirebaseScoreListener implements ValueEventListener {

    private ArrayList<userScore> playerScores;
    private QuizResultActivity quizResultActivity;
    private userScore currentPlayer;

    public FirebaseScoreListener(ArrayList<userScore> playerScores, userScore currentPlayer, QuizResultActivity quizResultActivity) {
        this.playerScores = playerScores;
        this.quizResultActivity = quizResultActivity;
        this.currentPlayer = currentPlayer;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        Log.d("dbtag", "ondatachangecalled");
        //read all users for that topic
        Iterable<DataSnapshot> children = snapshot.getChildren();
        children.forEach(i -> {
            playerScores.add(i.getValue(userScore.class));
        });
        playerScores.add(currentPlayer);

        quizResultActivity.updateList();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage());
    }
}
