// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.models;

import android.os.Build;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.O_MR1})
public class UserScoreTest extends TestCase {

    @Test
    public void getSetUserName() {
        UserScore userScore = new UserScore();
        userScore.setUserName("Test");
        assertEquals("Test", userScore.getUserName());
    }

    @Test
    public void getSetUserScore() {
        UserScore userScore = new UserScore("Test", 1);
        assertEquals(1, userScore.getUserScore());
        userScore.setUserScore(2);
        assertEquals(2, userScore.getUserScore());
    }
}