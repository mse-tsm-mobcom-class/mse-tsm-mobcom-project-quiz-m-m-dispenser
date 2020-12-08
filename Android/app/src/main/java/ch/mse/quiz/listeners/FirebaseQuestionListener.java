package ch.mse.quiz.listeners;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import ch.mse.quiz.QuestionActivity;
import ch.mse.quiz.models.Question;

import static android.content.ContentValues.TAG;

public class FirebaseQuestionListener implements ValueEventListener {

    private ArrayList<Question> questions;
    private int questionNumber;
    private final QuestionActivity questionActivity;

    public FirebaseQuestionListener(ArrayList<Question> questions, int questionNumber, QuestionActivity questionActivity) {
        this.questions = questions;
        this.questionNumber = questionNumber;
        this.questionActivity = questionActivity;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        int questionnr = (int) dataSnapshot.getChildrenCount();
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        children.forEach(i -> {
            questions.add(i.getValue(Question.class));
        });
        // if less questions are in the DB then chosen by the user
        if (questionnr <= questionNumber) {
            questionNumber = questionnr;
            //Toast.makeText(getBaseContext(), "Nr. of question adjusted. Only " + questionnr + " questions available.", Toast.LENGTH_SHORT).show();
        }
        questionActivity.questions = getRandomQuestions(questions, questionNumber);
        //set first question UI
        questionActivity.createQuestion(1);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.e(TAG, "onCancelled: Something went wrong! Error:" + databaseError.getMessage());
    }


    private ArrayList<Question> getRandomQuestions(ArrayList<Question> questions, int questionNumber) {
        ArrayList<Question> randomList = new ArrayList<>();
        int pointer = 0;
        boolean flag = true;
        for (int i = 0; i < questionNumber; i++) {
            // check if question is already chosen
            while (flag) {
                //get random number for the questios list
                pointer = getRandomNumber(0,questions.size() - 1);
                if (!randomList.contains(questions.get(pointer))) {
                    flag = false;
                }
            }
            randomList.add(questions.get(pointer));
            flag = true;
        }
        return randomList;
    }

    private int getRandomNumber(int lowerbound, int higherbound) {
        Random r = new Random();
        int low = lowerbound;
        int high = higherbound;
        int result = r.nextInt(high - low) + low;
        return result;
    }


}
