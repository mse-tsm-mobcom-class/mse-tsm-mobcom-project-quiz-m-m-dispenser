package ch.mse.quiz.models;


public class userScore {
    private String userName;
    private int score;

    public userScore() {

    }

    public userScore(String userName, int score) {
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
