package com.example.reemafinal2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
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

import java.util.concurrent.Executor;

public class Ai extends AppCompatActivity {

    private EditText etTopic;
    private MaterialButton btnSuggestSteps;
    private TextView tvAiResponse;
    private MaterialCardView responseCard;
    private GenerativeModelFutures model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai);

        // 1. Initialize UI components (Matched to your Glass XML IDs)
        etTopic = findViewById(R.id.topic);
        btnSuggestSteps = findViewById(R.id.btnSuggestSteps);
        tvAiResponse = findViewById(R.id.tvAiResponse);
        responseCard = findViewById(R.id.responseCard);

        // 2. Handle System Bar Padding (Matched your XML ID: header)
        View headerLayout = findViewById(R.id.header);
        if (headerLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(headerLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // 3. Initialize Firebase AI
        try {
            FirebaseAI aiInstance = FirebaseAI.getInstance(GenerativeBackend.googleAI());
            model = GenerativeModelFutures.from(aiInstance.generativeModel("gemini-3-preview"));
        } catch (Exception e) {
            Toast.makeText(this, "AI Setup Error", Toast.LENGTH_SHORT).show();
        }

        // 4. Generate Button Click
        btnSuggestSteps.setOnClickListener(v -> {
            String topic = etTopic.getText().toString().trim();
            if (!topic.isEmpty()) {
                generateAIPlan(topic);
            } else {
                Toast.makeText(this, "Enter a mission first!", Toast.LENGTH_SHORT).show();
            }
        });

        // 5. Long click to copy the plan
        tvAiResponse.setOnLongClickListener(v -> {
            copyToClipboard(tvAiResponse.getText().toString());
            return true;
        });
    }

    private void generateAIPlan(String topic) {
        if (model == null) return;

        // Visual feedback while loading
        tvAiResponse.setAlpha(0.3f);
        btnSuggestSteps.setEnabled(false);

        Content content = new Content.Builder()
                .addText("Act as a professional assistant. Provide a structured checklist for: " + topic)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Executor executor = this::runOnUiThread;
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                btnSuggestSteps.setEnabled(true);
                // Trigger modern animations
                animateResponse(result.getText());
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                btnSuggestSteps.setEnabled(true);
                tvAiResponse.setAlpha(1.0f);
                tvAiResponse.setText("Connection lost. Try again.");
            }
        }, executor);
    }

    // --- 2026 PRETTY ANIMATIONS ---
    private void animateResponse(String text) {
        // Smooth Fade-in
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);

        tvAiResponse.setText(text);
        tvAiResponse.setAlpha(1.0f);
        tvAiResponse.startAnimation(fadeIn);

        // Glass Card Pulse Effect
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
}


