package com.example.reemafinal2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * ReportProblemFragment: كلاس مسؤول عن استقبال شكاوى المستخدمين
 * وإرسالها إلى قاعدة بيانات Firebase Realtime Database.
 */
public class ReportProblemFragment extends Fragment {

    // عناصر واجهة المستخدم
    private EditText etProblemDescription; // حقل النص لكتابة المشكلة
    private MaterialButton btnSubmitReport; // زر الإرسال

    // مراجع Firebase
    private DatabaseReference mDatabase; // مرجع قاعدة البيانات
    private FirebaseAuth mAuth; // مرجع المصادقة لمعرفة المستخدم الحالي

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ربط ملف الواجهة XML الخاص بالشكاوى
        View view = inflater.inflate(R.layout.fragment_report_problem, container, false);

        // 1. تعريف العناصر البرمجية وربطها بالواجهة
        etProblemDescription = view.findViewById(R.id.etProblemDescription);
        btnSubmitReport = view.findViewById(R.id.btnSubmitReport);

        // 2. تهيئة أدوات Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // 3. برمجة زر الإرسال
        btnSubmitReport.setOnClickListener(v -> {
            String message = etProblemDescription.getText().toString().trim();

            // التحقق من أن النص ليس فارغاً
            if (!message.isEmpty()) {
                submitReport(message);
            } else {
                Toast.makeText(getContext(), "Please describe the problem", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    /**
     * دالة تقوم بتجهيز البيانات وإرسالها إلى السيرفر.
     * @param message نص المشكلة المكتوب من قبل المستخدم.
     */
    private void submitReport(String message) {
        // جلب معرف المستخدم الحالي، أو وضعه "anonymous" إذا لم يكن مسجلاً
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";

        // إنشاء مفتاح فريد جديد داخل جدول "reports"
        String reportId = mDatabase.child("reports").push().getKey();

        // تجهيز البيانات داخل الخريطة (Map)
        Map<String, Object> report = new HashMap<>();
        report.put("userId", userId);
        report.put("message", message);
        report.put("timestamp", System.currentTimeMillis()); // تسجيل وقت الإرسال

        // التحقق من نجاح إنشاء المعرف الفريد
        if (reportId != null) {
            // حفظ البيانات في المسار: reports/reportId
            mDatabase.child("reports").child(reportId).setValue(report)
                    .addOnSuccessListener(aVoid -> {
                        // في حالة النجاح
                        Toast.makeText(getContext(), "Problem Reported Successfully!", Toast.LENGTH_SHORT).show();

                        // العودة تلقائياً للشاشة السابقة (Settings)
                        getParentFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> {
                        // في حالة فشل الاتصال بالسيرفر
                        Toast.makeText(getContext(), "Failed to send report", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}