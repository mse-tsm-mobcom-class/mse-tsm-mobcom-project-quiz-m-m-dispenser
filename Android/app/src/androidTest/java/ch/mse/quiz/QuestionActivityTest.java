package ch.mse.quiz;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class QuestionActivityTest {
/*
    @Rule
    public ActivityTestRule<QuestionActivity> mActivityTest = new ActivityTestRule<QuestionActivity>(QuestionActivity.class);

*/
    @Rule
    public ActivityTestRule<QuestionActivity> mActivityTest = new ActivityTestRule<QuestionActivity>(QuestionActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Bundle extras = new Bundle();
            extras.putInt("QUESTION_COUNT", 4);
            extras.putString("QUESTION_TOPIC", "sports");

            Intent intent = new Intent(InstrumentationRegistry.getContext(),QuestionActivity.class);
            intent.putExtras(extras);
            return intent;
        }
    };


    @Test
    public void test_isActivityInView() {
        onView(withId(R.id.textView_dispenserState)).check(matches(isDisplayed()));
    }
/*
    @Test
    public void test_answerQuestion() {
        onView(withId(R.id.textView_answerA)).perform(click());

        //intended(hasComponent(QuestionActivity.class.getName()));
    }
    */

}