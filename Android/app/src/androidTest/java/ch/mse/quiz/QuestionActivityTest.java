// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuestionActivityTest {
    @Rule
    public ActivityTestRule<QuestionActivity> mActivityTest = new ActivityTestRule<QuestionActivity>(QuestionActivity.class, false, false);

    @Rule
    public IntentsTestRule<QuestionActivity> mActivityRule = new IntentsTestRule<QuestionActivity>(
            QuestionActivity.class, false, false);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    //successful activity launch
    @Test
    public void test_isActivityInView() {
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putInt(QuestionActivity.QUESTION_NUMBER, 5);
        extras.putString(QuestionActivity.QUIZ_TOPIC, "history");
        i.putExtras(extras);
        mActivityTest.launchActivity(i);

        onView(withId(R.id.textView_questionProgress)).check(matches(isDisplayed()));
    }

    @Test
    public void test_quizResultActivityIntent() {
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putInt(QuestionActivity.QUESTION_NUMBER, 1);
        extras.putString(QuestionActivity.QUIZ_TOPIC, "history");
        i.putExtras(extras);
        mActivityTest.launchActivity(i);

        onView(withId(R.id.textView_answerA)).perform(click());
        intended(allOf(
                hasComponent(QuizResultActivity.class.getName())
        ));
    }
}