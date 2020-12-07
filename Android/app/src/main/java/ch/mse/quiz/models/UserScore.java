// Copyright (c) 2020, Steiner Pascal, Strässle Nikolai, Radinger Martin
// All rights reserved.

// Licensed under LICENSE, see LICENSE file

package ch.mse.quiz.models;

public class UserScore {
    private String userName;
    private int score;

    public UserScore() {

    }

    public UserScore(String userName, int score) {
        this.userName = userName;
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserScore() {
        return score;
    }

    public void setUserScore(int score) {
        this.score = score;
    }

}
