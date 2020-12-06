package ch.mse.quiz.listeners;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.function.Function;

import androidx.annotation.NonNull;
import ch.mse.quiz.QuestionActivity;
import ch.mse.quiz.models.question;

import static android.content.ContentValues.TAG;

public class FirebaseQuestionListener implements ValueEventListener {

    private final ArrayList<question> questions;
    private int questionNumber;
    private QuestionActivity questionActivity;

    public FirebaseQuestionListener(ArrayList<question> questions, int questionNumber, QuestionActivity questionActivity) {
        this.questions = questions;
        this.questionNumber = questionNumber;
        this.questionActivity = questionActivity;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        int questionnr = (int) dataSnapshot.getChildrenCount();
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        children.forEach(i -> {
            questions.add(i.getValue(question.class));
        });
        // if less questions are in the DB then chosen by the user
        if (questionnr <= questionNumber) {
            questionNumber = questionnr;
            //Toast.makeText(getBaseContext(), "Nr. of question adjusted. Only " + questionnr + " questions available.", Toast.LENGTH_SHORT).show();
        }
        //set first question UI
        questionActivity.createQuestion(1);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage());
    }
}
