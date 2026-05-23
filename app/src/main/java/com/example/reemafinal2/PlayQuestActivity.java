package com.example.reemafinal2;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reemafinal2.data.MyTasksTable.MyQuest; // تأكدي من مسار الكلاس الصحيح عندك

import java.util.Locale;

public class PlayQuestActivity extends AppCompatActivity {

    private TextView tvQuestTitle, tvTimer, tvInstructions;
    private Button btnDone ;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quest);

        // 1. ربط العناصر
        tvQuestTitle = findViewById(R.id.tvQuestTitle);
        tvTimer = findViewById(R.id.tvTimer);
        tvInstructions = findViewById(R.id.tvInstructions);
        btnDone = findViewById(R.id.btnfinish);
        // 1. تعريف الزر وربطه بالـ XML
        Button btnStop = findViewById(R.id.btnStop);

        // متغير لمتابعة حالة العداد (هل هو متوقف أم يعمل)
        final boolean[] isPaused = {false};

        btnStop.setOnClickListener(v -> {
            if (countDownTimer != null) {
                if (!isPaused[0]) {
                    // إذا كان يعمل -> نقوم بإيقافه (Pause)
                    countDownTimer.cancel();
                    isPaused[0] = true;
                    btnStop.setText("RESUME"); // تغيير النص ليوضح إمكانية العودة
                    Toast.makeText(this, "Timer Paused", Toast.LENGTH_SHORT).show();
                } else {
                    // إذا كان متوقفاً -> نعيد تشغيله من الوقت المتبقي (Resume)
                    startTimer();
                    isPaused[0] = false;
                    btnStop.setText("STOP");
                    Toast.makeText(this, "Timer Resumed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 2. استقبال كائن المهمة بالكامل من الـ Adapter
        // تأكدي أن الاسم "QUEST_DATA" مطابق لما كتبتيه في الـ Adapter
        MyQuest currentQuest = (MyQuest) getIntent().getSerializableExtra("QUEST_DATA");

        if (currentQuest != null) {
            // عرض البيانات الحقيقية
            tvQuestTitle.setText(currentQuest.getTitle());
            tvInstructions.setText(currentQuest.getNote());

            // تحويل الوقت من المهمة (افترضنا أن الوقت مخزن كدقائق في الكائن)
            // إذا كان الوقت مخزن بصيغة "00:30"، سنقوم بتحويله لملي ثانية
            parseTimeAndStart(currentQuest.getTime());
        }

        // 3. برمجة زر "تم الإنجاز"
        btnDone.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            Toast.makeText(this, "Quest Completed! Well done!", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    /**
     * دالة لتحويل نص الوقت (مثل "05:00") إلى ميلي ثانية وبدء العداد
     */
    private void parseTimeAndStart(String timeStr) {
        try {
            if (timeStr != null && timeStr.contains(":")) {
                String[] parts = timeStr.split(":");
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                timeLeftInMillis = (minutes * 60L + seconds) * 1000;
            } else {
                timeLeftInMillis = 30000; // وقت افتراضي 30 ثانية إذا حصل خطأ
            }
        } catch (Exception e) {
            timeLeftInMillis = 30000;
        }
        startTimer();
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
        if (countDownTimer != null) countDownTimer.cancel();
    }
}