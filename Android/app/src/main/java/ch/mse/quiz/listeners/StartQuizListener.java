package ch.mse.quiz.listeners;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;

import ch.mse.quiz.QuestionActivity;
import ch.mse.quiz.R;
import ch.mse.quiz.ble.BleGattCallback;
import ch.mse.quiz.printes.ToastPrinter;

public class StartQuizListener implements View.OnClickListener {

    public static final String QUESTION_NUMBER = "ch.mse.quiz.extra.NUMBER";
    public static final String QUESTION_TOPIC = "ch.mse.quiz.extra.TOPIC";
    private final Activity activity;
    private final ToastPrinter toastPrinter;
    private final BleGattCallback bleGattCallback;
    private final String LOG_TAG = StartQuizListener.class.getCanonicalName();
    private final NumberPicker npTopic;
    private final NumberPicker npNumberOfQuestions;
    private final ArrayList<String> topicList;

    public StartQuizListener(Activity activity, ToastPrinter toastPrinter, BleGattCallback bleGattCallback, NumberPicker npTopic, NumberPicker npNumberOfQuestions, ArrayList<String> topicList) {
        this.activity = activity;
        this.toastPrinter = toastPrinter;
        this.bleGattCallback = bleGattCallback;
        this.npTopic = npTopic;
        this.npNumberOfQuestions = npNumberOfQuestions;
        this.topicList = topicList;
    }

    @Override
    public void onClick(View v) {
        /*
        if (!bleGattCallback.isConnected()) {
            toastPrinter.print(activity.getString(R.string.toastNoDispenserConnected));
            return;
        }
        */
        Log.d(LOG_TAG, "start Quiz!");

        int choice = npTopic.getValue();
        if (choice > topicList.size()) {
            choice = topicList.size();
        }
        String topic = topicList.get(choice == 0 ? 0 : choice - 1); // if choice is 0 take the first topic
        Bundle extras = new Bundle();
        extras.putInt(QUESTION_NUMBER, npNumberOfQuestions.getValue());
        extras.putString(QUESTION_TOPIC, topic);

        Intent intent = new Intent(activity, QuestionActivity.class);
        intent.putExtras(extras);

        activity.startActivity(intent);
        activity.finish();
    }
}
