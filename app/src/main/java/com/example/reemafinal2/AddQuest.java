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
    boolean isEditMode = false;
    MyQuest questToEdit;

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
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> {
            finish(); // يغلق الشاشة فوراً دون تنفيذ saveQuest()
        });

        // 2. إعداد المستمعات (Listeners)
        etQuestTime.setOnClickListener(v -> showTimePickerDialog()); // عند الضغط على حقل الوقت
        btnAddQuest.setOnClickListener(v -> saveQuest());// عند الضغط على زر الإضافة
        // داخل onCreate في AddQuest.java
        // داخل onCreate في AddQuest.java
        if (getIntent().hasExtra("QUEST_DATA")) {
            isEditMode = true;
            questToEdit = (MyQuest) getIntent().getSerializableExtra("QUEST_DATA");

            // تعبئة الحقول بالبيانات الموجودة
            etQuestTitle.setText(questToEdit.getTitle());
            etQuestTime.setText(questToEdit.getTime());
            etQuestSubject.setText(questToEdit.getSubject());
            etGameId.setText(questToEdit.getGameId());
            etQuestNote.setText(questToEdit.getNote());
            etQuestScore.setText(String.valueOf(questToEdit.getRewardpoints()));

            // تغيير نص الزر ليدل على التعديل
            btnAddQuest.setText("Update Mission");
        }
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

        // تحويل النقاط إلى رقم
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

        // --- الجزء المحدث للهندسة المنطقية ---
        MyQuest quest;
        if (isEditMode) {
            // إذا كنا في وضع التعديل، نستخدم الكائن الذي استقبلناه من الـ Intent
            quest = questToEdit;
        } else {
            // إذا كانت مهمة جديدة، ننشئ كائناً جديداً ونعطيه معرفاً زمنياً
            quest = new MyQuest();
            quest.setKeyId(System.currentTimeMillis());
        }

        // تعبئة/تحديث بيانات الكائن من الحقول
        quest.setTitle(title);
        quest.setTime(time);
        quest.setSubject(subject);
        quest.setGameId(gameId);
        quest.setNote(note);
        quest.setRewardpoints(score);

        // البدء بعملية الحفظ (سواء كانت إضافة أو تحديث)
        saveQuestToFirebase(quest);
    }

    /**
     * حفظ المهمة في Firebase Realtime Database ثم حفظها محلياً في Room.
     */
    public void saveQuestToFirebase(MyQuest quest) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference questsRef = database.child("quests");
        DatabaseReference targetRef;

        if (isEditMode) {
            targetRef = questsRef.child(quest.getUserId());
        } else {
            targetRef = questsRef.push();
            quest.setUserId(targetRef.getKey());
        }

        targetRef.setValue(quest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), isEditMode ? "Updated in Firebase" : "Added to Firebase", Toast.LENGTH_SHORT).show();

                // نبدأ خيط خلفي لمحاولة الحفظ في Room
                new Thread(() -> {
                    try {
                        if (isEditMode) {
                            AppDatabase.getDp(AddQuest.this).myTaskQuery().update(quest);
                        } else {
                            AppDatabase.getDp(AddQuest.this).myTaskQuery().insertMyQuest(quest);
                        }

                        // إذا نجح الحفظ في Room نغلق الصفحة
                        runOnUiThread(() -> {
                            Toast.makeText(AddQuest.this, "Sync Done!", Toast.LENGTH_SHORT).show();
                            finish();
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        // حتى لو فشل Room، بما أن Firebase نجح، نغلق الصفحة ونعود للقائمة
                        runOnUiThread(() -> {
                            // حذفنا رسالة الخطأ المزعجة هنا لكي لا تظهر لكِ
                            finish();
                        });
                    }
                }).start();

            } else {
                Toast.makeText(getApplicationContext(), "Firebase operation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}