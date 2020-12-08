package ch.mse.quiz.listeners;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import ch.mse.quiz.QuestionActivity;
import ch.mse.quiz.models.Question;

import static android.content.ContentValues.TAG;

public class FirebaseQuestionListener implements ValueEventListener {

    private final List<Question> questions;
    private int questionNumber;
    private final QuestionActivity questionActivity;
    private final SecureRandom randomSession = new SecureRandom();

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
        questionActivity.setQuestions(getRandomQuestions(questions, questionNumber));
        //set first question UI
        questionActivity.createQuestion(1);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage());
    }


    private List<Question> getRandomQuestions(List<Question> questions, int questionNumber) {
        List<Question> randomList = new ArrayList<>();
        int pointer = 0;
        boolean flag = true;
        for (int i = 0; i < questionNumber; i++) {
            // check if question is already chosen
            while (flag) {
                //get random number for the questios list
                pointer = getRandomNumber(questions.size() - 1);
                if (!randomList.contains(questions.get(pointer))) {
                    flag = false;
                }
            }
            randomList.add(questions.get(pointer));
            flag = true;
        }
        return randomList;
    }

    private int getRandomNumber(int higherbound) {
        int low = 0;
        return randomSession.nextInt(higherbound - low) + low;
    }


}
