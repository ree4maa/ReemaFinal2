package com.example.reemafinal2;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * PlayQuestActivity: النشاط المسؤول عن تجربة تنفيذ المهمة.
 * يحتوي على مؤقت زمني وتفاعل عند الإنجاز.
 */
public class PlayQuestActivity extends AppCompatActivity {

    // عناصر واجهة المستخدم
    private TextView tvQuestTitle, tvTimer, tvInstructions;
    private Button btnDone;

    // أدوات إدارة الوقت
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 30000; // مدة المهمة: 30 ثانية افتراضياً

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quest);

        // 1. ربط العناصر البرمجية بالـ IDs في XML
        tvQuestTitle = findViewById(R.id.tvQuestTitle);
        tvTimer = findViewById(R.id.tvTimer);
        tvInstructions = findViewById(R.id.tvInstructions);
        btnDone = findViewById(R.id.btnDone);

        // 2. استقبال البيانات القادمة من شاشة القائمة (Intent Extras)
        String title = getIntent().getStringExtra("QUEST_TITLE");
        if (title != null) {
            tvQuestTitle.setText(title); // عرض اسم المهمة التي تم اختيارها
        }

        // 3. بدء العداد التنازلي فور الدخول
        startTimer();

        // 4. برمجة زر "تم الإنجاز"
        btnDone.setOnClickListener(v -> {
            // إيقاف العداد عند ضغط الزر
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            // إظهار رسالة نجاح
            Toast.makeText(this, "Quest Completed! Well done!", Toast.LENGTH_LONG).show();

            // العودة التلقائية لشاشة القائمة
            finish();
        });
    }

    /**
     * دالة لبدء العد التنازلي وتحديث الواجهة كل ثانية.
     */
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText(); // تحديث النص الظاهر (00:00)
            }

            @Override
            public void onFinish() {
                // ما يحدث عند انتهاء الوقت تماماً
                tvTimer.setText("00:00");
                Toast.makeText(PlayQuestActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    /**
     * تحويل الوقت المتبقي إلى تنسيق دقائق وثواني مفهوم للمستخدم.
     */
    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        // تنسيق النص ليكون دائماً بصيغة رقمين (مثلاً 09:05 بدلاً من 9:5)
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
    }

    /**
     * دالة الأمان: إغلاق العداد عند تدمير النشاط لضمان عدم استهلاك البطارية أو الذاكرة.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}