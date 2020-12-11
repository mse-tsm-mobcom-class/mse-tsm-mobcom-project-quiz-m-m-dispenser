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
public class QuestionTest extends TestCase {

    @Test
    public void getCorrectAnswer() {
        Question question = new Question("Test", 1, "Test 1", "Test 2", "Test 3", "Test 4");
        assertEquals(1, question.getCorrectAnswer());
    }

    @Test
    public void getQuestion() {
        Question question = new Question("Test", 1, "Test 1", "Test 2", "Test 3", "Test 4");
        assertEquals("Test", question.getQuestionText());
    }

    @Test
    public void getAnswer1() {
        Question question = new Question();
        question.setAnswer1("Test 1");
        assertEquals("Test 1", question.getAnswer1());
    }

    @Test
    public void getAnswer2() {
        Question question = new Question();
        question.setAnswer2("Test 2");
        assertEquals("Test 2", question.getAnswer2());
    }

    @Test
    public void getAnswer3() {
        Question question = new Question();
        question.setAnswer3("Test 3");
        assertEquals("Test 3", question.getAnswer3());
    }

    @Test
    public void getAnswer4() {
        Question question = new Question();
        question.setAnswer4("Test 4");
        assertEquals("Test 4", question.getAnswer4());
    }

    @Test
    public void getQuestionText() {
        Question question = new Question();
        question.setQuestionText("Test QuestionText");
        assertEquals("Test QuestionText", question.getQuestionText());
    }
}