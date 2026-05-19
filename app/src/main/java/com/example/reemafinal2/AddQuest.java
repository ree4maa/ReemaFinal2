package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull; // تم تصحيح المكتبة
import android.widget.Button;
import android.widget.Toast;

import com.example.reemafinal2.data.AppDatabase;
import com.example.reemafinal2.data.MyTasksTable.MyQuest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

/**
 * AddQuest: النشاط المسؤول عن إنشاء مهام جديدة وحفظها.
 * يجمع بين إدخال البيانات، اختيار الوقت، والحفظ في Firebase و Room.
 */
public class AddQuest extends AppCompatActivity {

    // تعريف عناصر واجهة المستخدم لإدخال بيانات المهمة
    TextInputEditText etQuestTitle, etQuestTime, etQuestSubject, etGameId, etQuestNote, etQuestScore;
    Button btnAddQuest;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quest);

        // 1. ربط العناصر البرمجية بالـ IDs الموجودة في ملف XML
        etQuestTitle = findViewById(R.id.etQuestTitle);
        etQuestTime = findViewById(R.id.etQuestTime);
        etQuestSubject = findViewById(R.id.etQuestSubject);
        etGameId = findViewById(R.id.etGameId);
        etQuestNote = findViewById(R.id.etQuestNote);
        btnAddQuest = findViewById(R.id.btnAddQuest);
        etQuestScore = findViewById(R.id.etQuestScore);

        // 2. إعداد المستمعات (Listeners)
        etQuestTime.setOnClickListener(v -> showTimePickerDialog()); // عند الضغط على حقل الوقت
        btnAddQuest.setOnClickListener(v -> saveQuest()); // عند الضغط على زر الإضافة
    }

    /**
     * عرض نافذة اختيار الوقت (Time Picker) للمستخدم.
     */
    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    // تنسيق الوقت المختار ليظهر بصيغة 00:00
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    etQuestTime.setText(time);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    /**
     * تجميع البيانات من الحقول والتحقق من صحتها قبل الحفظ.
     */
    private void saveQuest() {
        String title = etQuestTitle.getText().toString().trim();
        String time = etQuestTime.getText().toString().trim();
        String subject = etQuestSubject.getText().toString().trim();
        String gameId = etGameId.getText().toString().trim();
        String note = etQuestNote.getText().toString().trim();

        // تحويل النقاط إلى رقم مع معالجة الاستثناءات
        int score = 0;
        if (!etQuestScore.getText().toString().isEmpty()) {
            try {
                score = Integer.parseInt(etQuestScore.getText().toString());
            } catch (NumberFormatException e) {
                score = 0;
            }
        }

        // التحقق من الحقول الإجبارية
        if (title.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in Title and Time", Toast.LENGTH_SHORT).show();
            return;
        }

        // إنشاء كائن المهمة وتعبئة البيانات
        MyQuest quest = new MyQuest();
        quest.setTitle(title);
        quest.setTime(time);
        quest.setSubject(subject);
        quest.setGameId(gameId);
        quest.setNote(note);
        quest.setRewardpoints(score);
        quest.setKeyId(System.currentTimeMillis()); // معرف فريد للمهمة

        // البدء بعملية الحفظ في السيرفر
        saveQuestToFirebase(quest);
    }

    /**
     * حفظ المهمة في Firebase Realtime Database ثم حفظها محلياً في Room.
     */
    public void saveQuestToFirebase(MyQuest quest) {
        // الوصول لمرجع "quests" في قاعدة البيانات
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference questsRef = database.child("quests");

        // إنشاء معرّف فريد جديد للمهمة في السحاب
        DatabaseReference newQuestRef = questsRef.push();
        quest.setUserId(newQuestRef.getKey()); // تخزين المعرف داخل الكائن

        // رفع البيانات للسيرفر
        newQuestRef.child(quest.getUserId()).setValue(quest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "FB Task added successfully", Toast.LENGTH_SHORT).show();

                    // بعد نجاح الرفع للسحابة، يتم الحفظ في قاعدة بيانات Room المحلية
                    new Thread(() -> {
                        try {
                            AppDatabase.getDp(AddQuest.this).myTaskQuery().insertMyQuest(quest);
                            runOnUiThread(() -> {
                                Toast.makeText(AddQuest.this, "Quest saved locally!", Toast.LENGTH_SHORT).show();
                                finish(); // إغلاق الشاشة والعودة للقائمة
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(AddQuest.this, "Error saving local: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();

                } else {
                    Toast.makeText(getApplicationContext(), "FB Failed to add task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}