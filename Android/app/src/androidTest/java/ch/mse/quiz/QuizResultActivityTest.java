// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.PositionAssertions.isRightOf;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuizResultActivityTest {

    @Rule
    public ActivityTestRule<QuizResultActivity> mActivityTest = new ActivityTestRule<QuizResultActivity>(QuizResultActivity.class, false, false);

    @Rule
    public IntentsTestRule<QuizResultActivity> intentsTestRule = new IntentsTestRule<QuizResultActivity>(
            QuizResultActivity.class, false, false);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void ensureRightOf() {
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putInt(QuestionActivity.SCORE, 3);
        extras.putInt(QuestionActivity.QUESTION_NUMBER, 5);
        extras.putString(QuestionActivity.QUIZ_TOPIC, "sports");
        i.putExtras(extras);

        mActivityTest.launchActivity(i);

        onView(ViewMatchers.withId(R.id.button_endQuiz)).check(isRightOf(withId(R.id.button_restartQuiz)));
    }

    //successful activity launch
    @Test
    public void test_isActivityInView() {
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putInt(QuestionActivity.SCORE, 3);
        extras.putInt(QuestionActivity.QUESTION_NUMBER, 5);
        extras.putString(QuestionActivity.QUIZ_TOPIC, "sports");
        i.putExtras(extras);

        mActivityTest.launchActivity(i);

        onView(withId(R.id.leaderboardLogo)).check(matches(isDisplayed()));
    }

    //press 'restart quiz button', launch new quiz intent
    @Test
    public void test_restartQuizIntent() {
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putInt(QuestionActivity.SCORE, 3);
        extras.putInt(QuestionActivity.QUESTION_NUMBER, 5);
        extras.putString(QuestionActivity.QUIZ_TOPIC, "sports");
        i.putExtras(extras);

        mActivityTest.launchActivity(i);

        onView(withId(R.id.button_restartQuiz)).perform(click());
        intended(allOf(
                hasComponent(MainActivity.class.getName())
        ));
    }

    //press 'end quiz' button, close app
    @Test
    public void test_endQuizIntent() {
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putInt(QuestionActivity.SCORE, 3);
        extras.putInt(QuestionActivity.QUESTION_NUMBER, 5);
        extras.putString(QuestionActivity.QUIZ_TOPIC, "sports");
        i.putExtras(extras);

        mActivityTest.launchActivity(i);

        onView(withId(R.id.button_endQuiz)).perform(click());
        assertTrue(mActivityTest.getActivity().isFinishing());
    }
}
