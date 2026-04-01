package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AIChatActivity extends AppCompatActivity {

    private TextView chatResponse;
    private EditText chatInput;
    private Button btnSend;
    private GenerativeModelFutures model;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        chatResponse = findViewById(R.id.chatResponse);
        chatInput = findViewById(R.id.chatInput);
        btnSend = findViewById(R.id.btnSend);

        // 1. Initialize Gemini Model (use "gemini-1.5-flash" for speed)
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyAbBRf41X8Wsgjtn2Op-u9saE2r4OC9qbY");
        model = GenerativeModelFutures.from(gm);

        btnSend.setOnClickListener(v -> {
            String userText = chatInput.getText().toString();
            if (!userText.isEmpty()) {
                sendMessageToAI(userText);
                chatInput.setText("");
                chatResponse.append("\n\nYou: " + userText);
            }
        });
    }

    private void sendMessageToAI(String text) {
        Content content = new Content.Builder().addText(text).build();

        // 2. Call the AI model asynchronously
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        // 3. Handle the response
        Executor executor = Executors.newSingleThreadExecutor();
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String aiResponse = result.getText();
                runOnUiThread(() -> {
                    chatResponse.append("\n\nAI: " + aiResponse);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    chatResponse.append("\n\nError: " + t.getMessage());
                });
            }
        }, executor);
    }
}