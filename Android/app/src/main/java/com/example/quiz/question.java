package com.example.quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class question {
    private String question;
    private int correctAnswer;
    private List<String> answers;

    public question(String question, int correctAnswer, String[] answers) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.answers = new ArrayList<>();
        this.answers.addAll(Arrays.asList(answers));
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer(int i) {
        return answers.get(i);
    }
}
