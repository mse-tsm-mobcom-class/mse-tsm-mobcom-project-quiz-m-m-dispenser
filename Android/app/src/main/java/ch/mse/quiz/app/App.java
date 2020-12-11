package ch.mse.quiz.app;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static List<Activity> activities = new ArrayList<>();

    public static void finish() {
        for (int i = 0; i < activities.size(); i++) {
            activities.get(i).finish();
            activities.remove(i);
        }
    }
}
