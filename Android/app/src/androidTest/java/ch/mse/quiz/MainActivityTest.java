package ch.mse.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.matcher.BundleMatchers;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
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
    public ActivityTestRule<MainActivity> mActivityTest = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test_isActivityInView() {
        onView(withId(R.id.tvLabelSelectQuestions)).check(matches(isDisplayed()));
    }
/*
    @Test
    public void test_questionActivityIntent() {
        onView(withId(R.id.button_startQuiz)).perform(click());

        Bundle extras = new Bundle();
        extras.putInt("QUESTION_COUNT", 4);
        extras.putString("QUESTION_TOPIC", "sports");

        Intent i = new Intent();
        i.putExtras(extras);
       // Instrumentation.ActivityMonitor monitor = new Instrumentation.ActivityMonitor();

        mActivityTest.launchActivity(i);
        intended(
                allOf(
                        hasComponent(QuestionActivity.class.getName()),
                        IntentMatchers.hasExtras(BundleMatchers.hasValue(hasExtras(BundleMatchers.hasValue("sports"))))
                )
        );

    }

    // Based on https://stackoverflow.com/questions/24074495/automating-number-picker-in-android-using-espresso
    @Test
    public void startQuiz() {
        //onView(withId(R.id.npQuestionTopic)).perform(typeText("This is a test."));
        onView(withId(R.id.button_startQuiz)).perform(click());
        //intended(IntentMatchers.hasExtras(BundleMatchers.hasKey("QUESTION_NUMBER")));
        //intended(IntentMatchers.hasExtras(BundleMatchers.hasKey("QUESTION_TOPIC")));

        ViewInteraction numPicker = onView(withId(R.id.npQuestionNumber));
        numPicker.perform(setNumber(1));
    }

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
*/
}