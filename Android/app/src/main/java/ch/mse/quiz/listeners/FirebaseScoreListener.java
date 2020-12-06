package ch.mse.quiz.listeners;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ch.mse.quiz.QuizResultActivity;
import ch.mse.quiz.models.UserScore;

import static android.content.ContentValues.TAG;

public class FirebaseScoreListener implements ValueEventListener {

    private final ArrayList<UserScore> playerScores;
    private final QuizResultActivity quizResultActivity;
    private final UserScore currentPlayer;

    public FirebaseScoreListener(ArrayList<UserScore> playerScores, UserScore currentPlayer, QuizResultActivity quizResultActivity) {
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
            playerScores.add(i.getValue(UserScore.class));
        });
        playerScores.add(currentPlayer);

        quizResultActivity.updateList();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage());
    }
}
