package ch.mse.quiz.listeners;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ch.mse.quiz.QuestionActivity;
import ch.mse.quiz.models.Question;

import static android.content.ContentValues.TAG;

public class FirebaseQuestionListener implements ValueEventListener {

    private final List<Question> questions;
    private int questionNumber;
    private final QuestionActivity questionActivity;

    public FirebaseQuestionListener(List<Question> questions, int questionNumber, QuestionActivity questionActivity) {
        this.questions = questions;
        this.questionNumber = questionNumber;
        this.questionActivity = questionActivity;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        int questionnr = (int) dataSnapshot.getChildrenCount();
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        children.forEach(i -> questions.add(i.getValue(Question.class)));
        // if less questions are in the DB then chosen by the user
        if (questionnr <= questionNumber) {
            questionNumber = questionnr;
        }
        //set first question UI
        questionActivity.createQuestion(1);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage());
    }
}
