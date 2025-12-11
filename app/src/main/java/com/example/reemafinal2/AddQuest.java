package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class AddQuest extends AppCompatActivity {

    // Declare the UI elements
    TextInputEditText etQuestTitle, etQuestTime, etQuestSubject, etGameId, etQuestNote;
    Button btnAddQuest;

    @SuppressLint("MissingInflatedId")
    @Override
    //هذه الدالة يتم استدعاؤها عند إنشاء الـ Activity لأول مرة.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quest);

        // 1. Initialize the UI elements by finding them in the layout
        etQuestTitle = findViewById(R.id.etQuestTitle);
        etQuestTime = findViewById(R.id.etQuestTime);
        etQuestSubject = findViewById(R.id.etQuestSubject);
        etGameId = findViewById(R.id.etGameId);
        etQuestNote = findViewById(R.id.etQuestNote);
        btnAddQuest = findViewById(R.id.btnAddQuest);

        // 2. Set an OnClickListener for the Time field
        etQuestTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        // 3. Set an OnClickListener for the "Add Quest" button
        btnAddQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuest();
            }
        });
    }

    // Method to show the Time Picker Dialog
    //	الحصول على الوقت الحالي من خلال .
    //• 	إنشاء TimePickerDialog مع الوقت الحالي كقيمة افتراضية.
    //• 	عند اختيار المستخدم للوقت، يتم تنسيقه على شكل  (24 ساعة) ووضعه في حقل النص .
    private void showTimePickerDialog() {
        // Get current time to set as default in the picker
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    // This block is called when the user sets a time
                    // Format the time and set it in the EditText
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    etQuestTime.setText(time);
                }, hour, minute, true); // true for 24-hour format

        timePickerDialog.show();
    }

    // Method to handle saving the quest data
    //دالة مسؤولة عن جمع البيانات من الحقول النصية والتحقق منها ثم حفظ المهمة (Quest).
    private void saveQuest() {
        // 4. Get the text from all the input fields
        String title = etQuestTitle.getText().toString().trim();
        String time = etQuestTime.getText().toString().trim();
        String subject = etQuestSubject.getText().toString().trim();
        String gameId = etGameId.getText().toString().trim();
        String note = etQuestNote.getText().toString().trim();

        // 5. Validate the required fields (e.g., Title and Time)
        if (title.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in at least the Title and Time", Toast.LENGTH_SHORT).show();
            return; // Stop the save process if validation fails
        }

        // --- DATABASE LOGIC GOES HERE ---
        // 6. At this point, you have all the data.
        // You would now create a new MyTask object and use your Room DAO to insert it.
        // For now, we'll just show a success message.

        Toast.makeText(this, "Quest '" + title + "' saved!", Toast.LENGTH_LONG).show();

        // Optional: Close the activity and go back to the previous screen
        finish();
    }
}
//- قراءة النصوص من الحقول: العنوان، الوقت، الموضوع، رقم اللعبة، الملاحظات.
//- التحقق من أن الحقول الأساسية (العنوان والوقت) ليست فارغة.
//- إذا كانت فارغة → إظهار رسالة خطأ باستخدام Toast.
//- إذا كانت صحيحة → إظهار رسالة نجاح (حالياً بدون قاعدة بيانات، مجرد رسالة).
//- إغلاق الـ Activity والعودة للشاشة السابقة باستخدام finish().