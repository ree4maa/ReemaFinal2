package com.example.reemafinal2;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class PlayQuestActivity extends AppCompatActivity {

    private TextView tvQuestTitle, tvTimer, tvInstructions;
    private Button btnDone;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 30000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quest);

        tvQuestTitle = findViewById(R.id.tvQuestTitle);
        tvTimer = findViewById(R.id.tvTimer);
        tvInstructions = findViewById(R.id.tvInstructions);
        btnDone = findViewById(R.id.btnDone);

        // Get data from intent
        String title = getIntent().getStringExtra("QUEST_TITLE");
        if (title != null) {
            tvQuestTitle.setText(title);
        }

        startTimer();

        btnDone.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            Toast.makeText(this, "Quest Completed! Well done!", Toast.LENGTH_LONG).show();
            finish(); // Go back to the list
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                Toast.makeText(PlayQuestActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
