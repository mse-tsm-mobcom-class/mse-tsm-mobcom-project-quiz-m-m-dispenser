// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz;

import android.app.Activity;
import android.content.Intent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.quiz.firebase.FirebaseLogin;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<FirebaseLogin> mActivityTestRule = new ActivityTestRule<>(FirebaseLogin.class, false, false);

    @Rule
    public IntentsTestRule<FirebaseLogin> intentsTestRule = new IntentsTestRule<>(FirebaseLogin.class, false, false);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");

    private static String getAlphaNumericString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void loginTest() throws InterruptedException {
        mActivityTestRule.launchActivity(new Intent());
        String randomEmailPrefix = getAlphaNumericString(7);

        ViewInteraction textInputEditText = onView(withId(R.id.fieldEmail));
        textInputEditText.perform(scrollTo(), replaceText(String.format("%s@test.com", randomEmailPrefix)), closeSoftKeyboard());

        ViewInteraction textInputEditText6 = onView(withId(R.id.fieldPassword));
        textInputEditText6.perform(scrollTo(), replaceText("A12345gfc"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(withId(R.id.emailCreateAccountButton));
        materialButton.perform(scrollTo(), click());

        Thread.sleep(2000);
        assertEquals(Activity.RESULT_OK, mActivityTestRule.getActivityResult().getResultCode());

    }
}
