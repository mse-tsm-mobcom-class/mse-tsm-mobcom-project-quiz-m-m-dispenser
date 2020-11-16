package ch.mse.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QuizResultActivity extends AppCompatActivity {

    TextView results;
    Button restartQuiz;
    int correctAnswers;
    int questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        correctAnswers = extras.getInt(QuestionActivity.SCORE);
        questions = extras.getInt(QuestionActivity.QUESTIONNUMBER);

        results = findViewById(R.id.textView_results);
        results.setText("Your score is " + correctAnswers + " out of " + questions + " questions");

        restartQuiz = findViewById(R.id.button_restartQuiz);
        restartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}