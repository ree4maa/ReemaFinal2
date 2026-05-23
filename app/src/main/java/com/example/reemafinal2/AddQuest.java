package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
 * <h1>نشاط إضافة وتعديل المهام (AddQuest)</h1>
 * المسؤول عن واجهة المستخدم لإدخال بيانات المهمة (العنوان، الوقت، النقاط، إلخ).
 * يقوم بمعالجة البيانات وحفظها في قاعدة بيانات Firebase السحابية وقاعدة بيانات Room المحلية.
 *
 * @author Reema
 * @version 2.0
 */
public class AddQuest extends AppCompatActivity {

    /** حقول إدخال النصوص لبيانات المهمة */
    TextInputEditText etQuestTitle, etQuestTime, etQuestSubject, etGameId, etQuestNote, etQuestScore;

    /** زر حفظ أو تحديث المهمة */
    Button btnAddQuest;

    /** متغير منطقي لتحديد ما إذا كان النشاط في وضع التعديل أم إضافة جديدة */
    boolean isEditMode = false;

    /** كائن المهمة المراد تعديله في حال كان وضع التعديل مفعلاً */
    MyQuest questToEdit;

    /**
     * دالة إنشاء النشاط وإعداد واجهة المستخدم.
     * يتم فيها ربط العناصر، إعداد المستمعات، وفحص ما إذا كان هناك بيانات مرسلة للتعديل.
     *
     * @param savedInstanceState حالة النشاط المحفوظة
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quest);

        // 1. ربط العناصر البرمجية بالـ IDs الموجودة في ملف XML
        initializeViews();

        // 2. إعداد المستمع لزر الإلغاء
        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> finish());

        // 3. إعداد المستمعات لحقول الإدخال والأزرار
        etQuestTime.setOnClickListener(v -> showTimePickerDialog());
        btnAddQuest.setOnClickListener(v -> saveQuest());

        // 4. فحص Intent لاستقبال بيانات المهمة في حال التعديل
        checkForEditMode();
    }

    /**
     * ربط متغيرات الجافا بالعناصر المرئية في ملف XML.
     */
    private void initializeViews() {
        etQuestTitle = findViewById(R.id.etQuestTitle);
        etQuestTime = findViewById(R.id.etQuestTime);
        etQuestSubject = findViewById(R.id.etQuestSubject);
        etGameId = findViewById(R.id.etGameId);
        etQuestNote = findViewById(R.id.etQuestNote);
        btnAddQuest = findViewById(R.id.btnAddQuest);
        etQuestScore = findViewById(R.id.etQuestScore);
    }

    /**
     * فحص البيانات القادمة من النشاط السابق.
     * إذا وُجدت بيانات مهمة، يتم تعبئة الحقول وتغيير نمط الصفحة إلى "تعديل".
     */
    private void checkForEditMode() {
        if (getIntent().hasExtra("QUEST_DATA")) {
            isEditMode = true;
            questToEdit = (MyQuest) getIntent().getSerializableExtra("QUEST_DATA");

            etQuestTitle.setText(questToEdit.getTitle());
            etQuestTime.setText(questToEdit.getTime());
            etQuestSubject.setText(questToEdit.getSubject());
            etGameId.setText(questToEdit.getGameId());
            etQuestNote.setText(questToEdit.getNote());
            etQuestScore.setText(String.valueOf(questToEdit.getRewardpoints()));

            btnAddQuest.setText("Update Mission");
        }
    }

    /**
     * عرض نافذة اختيار الوقت (Time Picker) للمستخدم.
     * تقوم بتنسيق الوقت المختار بصيغة (HH:mm) ووضعه في حقل إدخال الوقت.
     */
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

    /**
     * دالة التحقق من صحة البيانات وبدء عملية الحفظ.
     * تقوم بجمع النصوص من الحقول، التحقق من الحقول الإجبارية، وإنشاء أو تحديث كائن {@link MyQuest}.
     */
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

        MyQuest quest;
        if (isEditMode) {
            quest = questToEdit;
        } else {
            quest = new MyQuest();
            quest.setKeyId(System.currentTimeMillis());
        }

        quest.setTitle(title);
        quest.setTime(time);
        quest.setSubject(subject);
        quest.setGameId(gameId);
        quest.setNote(note);
        quest.setRewardpoints(score);

        saveQuestToFirebase(quest);
    }

    /**
     * حفظ المهمة في قاعدة بيانات Firebase.
     * عند نجاح الحفظ السحابي، يتم استدعاء الخيط الخلفي لحفظ البيانات في قاعدة Room المحلية.
     *
     * @param quest كائن المهمة الذي يحتوي على كافة البيانات المراد حفظها.
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

                // حفظ البيانات محلياً في خيط منفصل لتجنب تعليق واجهة المستخدم
                saveQuestToRoom(quest);

            } else {
                Toast.makeText(getApplicationContext(), "Firebase operation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * حفظ المهمة في قاعدة البيانات المحلية (Room).
     * يتم إغلاق النشاط والعودة للقائمة فور الانتهاء أو في حال حدوث خطأ بعد نجاح Firebase.
     *
     * @param quest كائن المهمة المراد حفظه محلياً.
     */
    private void saveQuestToRoom(MyQuest quest) {
        new Thread(() -> {
            try {
                if (isEditMode) {
                    AppDatabase.getDp(AddQuest.this).myTaskQuery().update(quest);
                } else {
                    AppDatabase.getDp(AddQuest.this).myTaskQuery().insertMyQuest(quest);
                }

                runOnUiThread(() -> {
                    Toast.makeText(AddQuest.this, "Sync Done!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                // العودة للقائمة حتى لو فشل Room لأن البيانات حُفظت في Firebase
                runOnUiThread(this::finish);
            }
        }).start();
    }
}