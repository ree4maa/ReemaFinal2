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

public class Ai extends AppCompatActivity {

    private EditText etTopic;
    private MaterialButton btnSuggestSteps;
    private TextView tvAiResponse;
    private MaterialCardView responseCard;
    private GenerativeModelFutures model;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai);

        // 1. ربط العناصر بالواجهة (IDs من ملف XML الخاص بك)
        etTopic = findViewById(R.id.topic);
        btnSuggestSteps = findViewById(R.id.btnSuggestSteps);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        responseCard = findViewById(R.id.responseCard);

        // 2. ضبط الهوامش العلوية (StatusBar)
        View headerLayout = findViewById(R.id.header);
        if (headerLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // في onCreate استبدل الجزء رقم 3 بهذا:
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "English language not supported", Toast.LENGTH_SHORT).show();
                } else {
                    // اختبار صوتي بمجرد التشغيل للتأكد
                    tts.speak("AI Voice System Ready", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            } else {
                Toast.makeText(this, "TTS Initialization Failed", Toast.LENGTH_SHORT).show();
            }
        });

        // 4. تهيئة الذكاء الاصطناعي (Gemini AI)
        try {
            FirebaseAI aiInstance = FirebaseAI.getInstance(GenerativeBackend.googleAI());
            // استخدمنا 1.5-flash لأنه الأسرع والأكثر استقراراً حالياً
            model = GenerativeModelFutures.from(aiInstance.generativeModel("gemini-2.5-flash-lite"));
        } catch (Exception e) {
            Toast.makeText(this, "AI Setup Error", Toast.LENGTH_SHORT).show();
        }

        // 5. برمجة زر طلب المساعدة
        btnSuggestSteps.setOnClickListener(v -> {
            String topic = etTopic.getText().toString().trim();
            if (!topic.isEmpty()) {
                generateAIPlan(topic);
            } else {
                Toast.makeText(this, "Enter a mission first!", Toast.LENGTH_SHORT).show();
            }
        });

        // 6. نسخ النص عند الضغط المطول على الإجابة
        tvAiResponse.setOnLongClickListener(v -> {
            copyToClipboard(tvAiResponse.getText().toString());
            return true;
        });
    }

    private void generateAIPlan(String topic) {
        if (model == null) return;

        // تأثير بصري أثناء التحميل
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

                // 1. تحديث الواجهة والأنيميشن
                animateResponse(responseText);

                // 2. النطق الصوتي "المنظف الشامل"
                if (responseText != null && tts != null) {

                    // تنظيف النص من كل شيء (نجوم، أقواس، نقاط، شرطات، مربعات، علامات تعجب، إلخ)
                    String cleanText = responseText
                            .replaceAll("[\\*\\#\\(\\)\\[\\]\\{\\}\\-_+=/\\\\|> <~\\!\\?@\\$%\\^&\\.]", " ")
                            .replaceAll("\\s+", " ") // تحويل أي مسافات زائدة لمسافة واحدة
                            .trim();

                    // التأكد من أن النص ليس فارغاً بعد التنظيف
                    if (!cleanText.isEmpty()) {
                        Bundle params = new Bundle();
                        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f);

                        // استخدام QUEUE_FLUSH لقطع أي صوت قديم والبدء فوراً
                        tts.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "AI_RESPONSE_ID");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                btnSuggestSteps.setEnabled(true);
                tvAiResponse.setAlpha(1.0f);
                tvAiResponse.setText("Error: " + t.getMessage());
                Toast.makeText(Ai.this, "AI Error!", Toast.LENGTH_LONG).show();
            }
        }, executor);
    }

    // --- أنيميشن حديثة (2026 Style) ---
    private void animateResponse(String text) {
        // تأثير الظهور التدريجي للنص
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);

        tvAiResponse.setText(text);
        tvAiResponse.setAlpha(1.0f);
        tvAiResponse.startAnimation(fadeIn);

        // تأثير النبض للبطاقة الزجاجية
        if (responseCard != null) {
            responseCard.animate()
                    .scaleX(1.02f)
                    .scaleY(1.02f)
                    .setDuration(250)
                    .withEndAction(() -> responseCard.animate().scaleX(1f).scaleY(1f).setDuration(250).start())
                    .start();
        }
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("AI Plan", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Plan copied to clipboard! ✨", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        // إغلاق محرك النطق عند الخروج من الصفحة لتوفير الذاكرة
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}