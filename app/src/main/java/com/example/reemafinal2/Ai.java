package com.example.reemafinal2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;

import java.util.Locale;
import java.util.concurrent.Executor;

/**
 * Ai Activity: كلاس المساعد الذكي الذي يربط بين Gemini AI ومحرك النطق الصوتي.
 */
public class Ai extends AppCompatActivity {

    private EditText etTopic;
    private MaterialButton btnSuggestSteps;
    private TextView tvAiResponse;
    private MaterialButton btnStopSpeech;
    private MaterialCardView responseCard;

    // مرجع نموذج الذكاء الاصطناعي
    private GenerativeModelFutures model;
    // محرك تحويل النص إلى صوت
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // تفعيل مظهر الشاشة الكاملة الحديث
        setContentView(R.layout.activity_ai);

        // 1. ربط عناصر الواجهة
        etTopic = findViewById(R.id.topic);
        btnSuggestSteps = findViewById(R.id.btnSuggestSteps);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        responseCard = findViewById(R.id.responseCard);
        btnStopSpeech = findViewById(R.id.btnStopSpeech);

        // 2. معالجة هوامش شريط الحالة (StatusBar) لضمان عدم تداخل التصميم
        View headerLayout = findViewById(R.id.header);
        if (headerLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // 3. تهيئة محرك النطق الصوتي (TTS)
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    // رسالة ترحيبية صوتية للتأكد من جاهزية النظام
                    tts.speak("AI Voice System Ready", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });

        // 4. إعداد اتصال Firebase بالذكاء الاصطناعي (Gemini)
        try {
            FirebaseAI aiInstance = FirebaseAI.getInstance(GenerativeBackend.googleAI());
            // استخدام نسخة الـ Flash لضمان سرعة الرد
            model = GenerativeModelFutures.from(aiInstance.generativeModel("gemini-2.5-flash-lite"));
        } catch (Exception e) {
            Toast.makeText(this, "AI Setup Error", Toast.LENGTH_SHORT).show();
        }

        // 5. حدث النقر لطلب الخطة من الـ AI
        btnSuggestSteps.setOnClickListener(v -> {
            String topic = etTopic.getText().toString().trim();
            if (!topic.isEmpty()) {
                generateAIPlan(topic);
            } else {
                Toast.makeText(this, "Enter a mission first!", Toast.LENGTH_SHORT).show();
            }
        });

        // 6. ميزة النسخ السريع عند الضغط المطول على الإجابة
        tvAiResponse.setOnLongClickListener(v -> {
            copyToClipboard(tvAiResponse.getText().toString());
            return true;
        });

        btnStopSpeech.setOnClickListener(v -> {
            if (tts != null) {
                tts.stop(); // يوقف الكلام فوراً
                Toast.makeText(this, "Speech Muted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * إرسال الطلب للـ AI ومعالجة الرد صوتياً وبصرياً.
     */
    private void generateAIPlan(String topic) {
        if (model == null) return;

        // تهيئة الواجهة لحالة الانتظار
        tvAiResponse.setAlpha(0.3f);
        tvAiResponse.setText("Thinking...");
        btnSuggestSteps.setEnabled(false);

        Content content = new Content.Builder()
                .addText("Act as a professional assistant. Provide a short, clear structured checklist for: " + topic)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Executor executor = this::runOnUiThread;

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String responseText = result.getText();
                btnSuggestSteps.setEnabled(true);

                // تحديث النص مع أنيميشن الظهور
                animateResponse(responseText);

                // معالجة النطق الصوتي
                if (responseText != null && tts != null) {
                    // إزالة الرموز التقنية لضمان نطق سلس
                    String cleanText = responseText
                            .replaceAll("[\\*\\#\\(\\)\\[\\]\\{\\}\\-_+=/\\\\|> <~\\!\\?@\\$%\\^&\\.]", " ")
                            .replaceAll("\\s+", " ")
                            .trim();

                    if (!cleanText.isEmpty()) {
                        Bundle params = new Bundle();
                        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f);
                        // نطق النص الجديد فوراً وإلغاء أي نطق قديم
                        tts.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "AI_ID");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                btnSuggestSteps.setEnabled(true);
                tvAiResponse.setAlpha(1.0f);
                tvAiResponse.setText("Error: " + t.getMessage());
            }
        }, executor);
    }

    /**
     * تطبيق تأثيرات بصرية حديثة (Fade & Pulse) على الرد.
     */
    private void animateResponse(String text) {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        tvAiResponse.setText(text);
        tvAiResponse.setAlpha(1.0f);
        tvAiResponse.startAnimation(fadeIn);

        if (responseCard != null) {
            // حركة نبض بسيطة للبطاقة لجذب الانتباه
            responseCard.animate()
                    .scaleX(1.02f)
                    .scaleY(1.02f)
                    .setDuration(250)
                    .withEndAction(() -> responseCard.animate().scaleX(1f).scaleY(1f).setDuration(250).start())
                    .start();
        }
    }

    /**
     * نسخ الخطة المولدة إلى حافظة الجهاز.
     */
    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("AI Plan", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Plan copied! ✨", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        // تنظيف الذاكرة وإغلاق محرك الصوت فور إغلاق الصفحة
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
