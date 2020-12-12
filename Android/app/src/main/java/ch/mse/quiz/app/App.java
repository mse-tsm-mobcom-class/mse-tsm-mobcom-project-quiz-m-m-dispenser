// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.app;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class App {
    private static final List<Activity> activities = new ArrayList<>();

    private App() {
    }

    public static void finish() {
        for (int i = 0; i < activities.size(); i++) {
            activities.get(i).finish();
        }
        activities.clear();
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static int getActivitySize() {
        return activities.size();
    }
}
