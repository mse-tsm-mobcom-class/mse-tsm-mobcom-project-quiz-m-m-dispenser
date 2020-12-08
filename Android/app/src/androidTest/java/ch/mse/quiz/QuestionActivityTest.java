// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.mse.quiz.listeners.StartQuizListener;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuestionActivityTest extends UITestHelper {
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
        i.putExtra(StartQuizListener.QUESTION_NUMBER, 1);
        i.putExtra(StartQuizListener.QUESTION_TOPIC, "history");
        mActivityTest.launchActivity(i);

        onView(withId(R.id.textView_questionProgress)).check(matches(isDisplayed()));
    }

    @Test
    public void test_quizResultActivityIntent() {
        Intent i = new Intent();
        i.putExtra(StartQuizListener.QUESTION_NUMBER, 1);
        i.putExtra(StartQuizListener.QUESTION_TOPIC, "history");
        mActivityTest.launchActivity(i);

        int correctAnswerButtonId = R.id.textView_answerA;
        if (2 == mActivityTest.getActivity().getCorrectAnswer()) {
            correctAnswerButtonId = R.id.textView_answerB;
        } else if (3 == mActivityTest.getActivity().getCorrectAnswer()) {
            correctAnswerButtonId = R.id.textView_answerC;
        } else if (4 == mActivityTest.getActivity().getCorrectAnswer()) {
            correctAnswerButtonId = R.id.textView_answerD;

        }

        onView(withId(correctAnswerButtonId)).perform(click());
        intended(
                hasComponent(hasClassName(QuizResultActivity.class.getName()))
        );
    }
}