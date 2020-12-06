package ch.mse.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.BundleMatchers;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasCategories;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasKey;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTest = new ActivityTestRule<MainActivity>(MainActivity.class, false, false);

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<MainActivity>(MainActivity.class, false, false);

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
        mActivityTest.launchActivity(i);

        onView(withId(R.id.mainLogo)).check(matches(isDisplayed()));
    }

    //test: launch new quiz intent
    @Test
    public void test_startQuizIntent() {
        Intent i = new Intent();
        mActivityTest.launchActivity(i);

        ViewInteraction numPicker = onView(withId(R.id.npQuestionNumber));
        numPicker.perform(setNumber(5));
        ViewInteraction topicPicker = onView(withId(R.id.npQuestionTopic));
        topicPicker.perform(setNumber(2));

        onView(withId(R.id.button_startQuiz)).perform(click());
        intended(allOf(
                hasComponent(QuestionActivity.class.getName())
                //hasExtras(BundleMatchers.hasEntry("QUESTION_NUMBER", 5)),
                //hasExtras(BundleMatchers.hasEntry("QUESTION_TOPIC", "history"))
        ));
    }

    // Based on https://stackoverflow.com/questions/24074495/automating-number-picker-in-android-using-espresso
    public static ViewAction setNumber(final int num) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                NumberPicker np = (NumberPicker) view;
                np.setValue(num);

            }

            @Override
            public String getDescription() {
                return "Set the passed number into the NumberPicker";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }
        };
    }
}