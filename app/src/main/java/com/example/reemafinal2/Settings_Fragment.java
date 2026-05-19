package com.example.reemafinal2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.AppDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executors;

/**
 * Settings_Fragment: يتحكم في واجهة الإعدادات.
 * يوفر وظائف مثل عرض الملف الشخصي، الدعم الفني، وتسجيل الخروج.
 */
public class Settings_Fragment extends Fragment {

    // عناصر واجهة المستخدم
    private TextView tvUserEmail;
    private View btnHelp, btnReportProblem, btnLogout;

    // أدوات Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // قاعدة البيانات المحلية (Room)
    private AppDatabase localDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ربط ملف الـ XML (fragment_settings_) بهذا الكلاس
        View view = inflater.inflate(R.layout.fragment_settings_, container, false);

        // 1. تعريف وربط العناصر بالـ IDs الموجودة في الـ XML
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnHelp = view.findViewById(R.id.btnHelp);
        btnReportProblem = view.findViewById(R.id.btnReportProblem);
        btnLogout = view.findViewById(R.id.btnLogout);

        // 2. تهيئة خدمات Firebase و Room
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        localDb = AppDatabase.getDp(getContext());

        // 3. عرض بريد المستخدم الحالي (إذا كان مسجلاً)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvUserEmail.setText(currentUser.getEmail());
        }

        // إعداد المستمعات للنقرات على الأزرار
        setupClickListeners();

        return view;
    }

    /**
     * إعداد كافة عمليات النقر (Click Listeners) للأزرار في الواجهة
     */
    private void setupClickListeners() {
        // زر المساعدة: يعرض رسالة توست للتواصل مع الدعم
        btnHelp.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Help Center: Contact support@reema.com", Toast.LENGTH_LONG).show();
        });

        // زر الإبلاغ عن مشكلة: ينتقل إلى Fragment آخر مخصص للإبلاغ
        btnReportProblem.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ReportProblemFragment())
                    .addToBackStack(null) // للسماح للمستخدم بالعودة للإعدادات عند الضغط على "رجوع"
                    .commit();
        });

        // زر تسجيل الخروج: يستدعي دالة الخروج النهائي
        btnLogout.setOnClickListener(v -> {
            performLogout();
        });
    }

    /**
     * تنفيذ عملية تسجيل الخروج بشكل كامل وآمن.
     * تشمل مسح البيانات المحلية والخروج من Firebase.
     */
    private void performLogout() {
        // 1. مسح قاعدة بيانات Room المحلية في خيط خلفي (Background Thread) لتجنب تجميد التطبيق
        Executors.newSingleThreadExecutor().execute(() -> {
            localDb.clearAllTables(); // مسح الـ Cache المحلي والبيانات المخزنة

            // 2. العودة للخيط الرئيسي (Main Thread) لتحديث واجهة المستخدم
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // تسجيل الخروج من Firebase
                    mAuth.signOut();
                    Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();

                    // 3. إعادة توجيه المستخدم إلى شاشة الاشتراك (SignUp)
                    // استخدام Flags لمسح سجل الشاشات السابقة ومنع المستخدم من الرجوع للإعدادات بعد الخروج
                    Intent intent = new Intent(getActivity(), SignUp.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish(); // إغلاق النشاط (Activity) الحالي
                });
            }
        });
    }
}