package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.reemafinal2.data.AppDatabase;
import com.example.reemafinal2.data.MyTasksTable.MyQuest;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class AddQuest extends AppCompatActivity {

    TextInputEditText etQuestTitle, etQuestTime, etQuestSubject, etGameId, etQuestNote, etQuestScore;
    Button btnAddQuest;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quest);

        etQuestTitle = findViewById(R.id.etQuestTitle);
        etQuestTime = findViewById(R.id.etQuestTime);
        etQuestSubject = findViewById(R.id.etQuestSubject);
        etGameId = findViewById(R.id.etGameId);
        etQuestNote = findViewById(R.id.etQuestNote);
        btnAddQuest = findViewById(R.id.btnAddQuest);
        etQuestScore = findViewById(R.id.etQuestScore);

        etQuestTime.setOnClickListener(v -> showTimePickerDialog());
        btnAddQuest.setOnClickListener(v -> saveQuest());
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    etQuestTime.setText(time);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    private void saveQuest() {
        String title = etQuestTitle.getText().toString().trim();
        String time = etQuestTime.getText().toString().trim();
        String subject = etQuestSubject.getText().toString().trim();
        String gameId = etGameId.getText().toString().trim();
        String note = etQuestNote.getText().toString().trim();

        int score = 0;
        if (!etQuestScore.getText().toString().isEmpty()) {
            try {
                score = Integer.parseInt(etQuestScore.getText().toString());
            } catch (NumberFormatException e) {
                score = 0;
            }
        }

        if (title.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in Title and Time", Toast.LENGTH_SHORT).show();
            return;
        }

        MyQuest quest = new MyQuest();
        quest.setTitle(title);
        quest.setTime(time);
        quest.setSubject(subject);
        quest.setGameId(gameId);
        quest.setNote(note);
        quest.setRewardpoints(score);
        
        // Generate a local unique ID using current time
        quest.setKeyid(System.currentTimeMillis()); 

        // Save only to local Room database
        new Thread(() -> {
            try {
                AppDatabase.getDp(AddQuest.this).myTaskQuery().insertMyQuest(quest);
                runOnUiThread(() -> {
                    Toast.makeText(AddQuest.this, "Quest saved locally!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddQuest.this, "Error saving quest: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
