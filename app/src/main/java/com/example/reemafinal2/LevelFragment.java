package com.example.reemafinal2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.AppDatabase;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

/**
 * LevelFragment: المسؤول عن عرض إحصائيات مستوى اللاعب، نقاط الخبرة (XP)، وصورة الملف الشخصي.
 */
public class LevelFragment extends Fragment {

    // عناصر الواجهة
    private ShapeableImageView imgProfile; // صورة الملف الشخصي (بزوايا مقصوصة)
    private TextView tvPlayerName, tvCurrentLevel, tvXpPercentage;
    private ProgressBar xpProgressBar; // شريط عرض التقدم في المستوى

    // مراجع قواعد البيانات
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private AppDatabase localDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ربط ملف الـ XML
        View view = inflater.inflate(R.layout.fragment_level, container, false);

        // 1. تعريف العناصر وربطها بالـ IDs
        imgProfile = view.findViewById(R.id.imgProfile);
        tvPlayerName = view.findViewById(R.id.tvPlayerName);
        tvCurrentLevel = view.findViewById(R.id.tvCurrentLevel);
        tvXpPercentage = view.findViewById(R.id.tvXpPercentage);
        xpProgressBar = view.findViewById(R.id.xpProgressBar);

        // 2. تهيئة قواعد البيانات (سحابية ومحلية)
        mAuth = FirebaseAuth.getInstance();
        localDb = AppDatabase.getDp(getContext());

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            // تحديد مسار المستخدم في Firebase Realtime Database
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // جلب البيانات من السيرفر ومزامنتها محلياً
            loadAndSyncData();
        } else {
            // في حال عدم وجود إنترنت أو تسجيل دخول، يتم الاعتماد على البيانات المحلية
            loadLocalDataOnly();
        }

        return view;
    }

    /**
     * الاستماع لتغييرات البيانات في Firebase وتحديث الواجهة والمزامنة مع Room.
     */
    private void loadAndSyncData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // جلب القيم من الـ Snapshot
                    String name = snapshot.child("name").getValue(String.class);
                    Long level = snapshot.child("level").getValue(Long.class);
                    Long xp = snapshot.child("xp").getValue(Long.class);
                    String profilePicUrl = snapshot.child("profilePic").getValue(String.class);

                    // تحديث واجهة المستخدم فوراً
                    updateUI(name, level, xp, profilePicUrl);

                    // حفظ نسخة احتياطية في قاعدة بيانات الجهاز (خلفية التطبيق)
                    syncToLocal(name, level, xp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // في حال فشل الاتصال، يتم الانتقال لوضع البيانات المحلية
                loadLocalDataOnly();
            }
        });
    }

    /**
     * توزيع البيانات المستلمة على عناصر الشاشة.
     */
    private void updateUI(String name, Long level, Long xp, String profilePicUrl) {
        tvPlayerName.setText(name != null ? name : "Player");
        tvCurrentLevel.setText(String.valueOf(level != null ? level : 1));

        int currentXp = (xp != null) ? xp.intValue() : 0;
        xpProgressBar.setMax(100); // القيمة القصوى للمستوى الحالي
        xpProgressBar.setProgress(currentXp);
        tvXpPercentage.setText(currentXp + " / 100");

        // استخدام Picasso لتحميل الصورة بذكاء مع صورة افتراضية في حال التأخير
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.get()
                    .load(profilePicUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgProfile);
        }
    }

    /**
     * حفظ البيانات المستلمة من السيرفر داخل قاعدة بيانات Room لضمان توفرها Offline.
     */
    private void syncToLocal(String name, Long level, Long xp) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // ملاحظة: يتم هنا استدعاء الـ DAO الخاص بك لتحديث بيانات المستخدم
            // Example: localDb.userDao().updateUserData(name, level, xp);
        });
    }

    /**
     * جلب البيانات من قاعدة البيانات المحلية Room في حال عدم توفر اتصال بالإنترنت.
     */
    private void loadLocalDataOnly() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // يتم استرجاع البيانات من الجهاز وعرضها عبر الـ UI Thread
            // MyUser user = localDb.userDao().getUser();
            // getActivity().runOnUiThread(() -> updateUI(user.name, ...));
        });
    }

    /**
     * دالة لتحديث شريط التقدم بشكل مخصص.
     */
    private void updateXPUI(int currentXp, int maxXp) {
        xpProgressBar.setMax(maxXp);
        xpProgressBar.setProgress(currentXp);
        tvXpPercentage.setText(currentXp + " / " + maxXp);
    }
}