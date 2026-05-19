package com.example.reemafinal2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity: النشاط الرئيسي والمحرك الأساسي للتنقل في التطبيق.
 * يقوم بإدارة تبديل الـ Fragments باستخدام شريط التنقل السفلي.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ربط الواجهة الرئيسية التي تحتوي على الحاوية وشريط التنقل
        setContentView(R.layout.activity_main);

        // 1. التحميل الافتراضي عند تشغيل التطبيق:
        // إذا كانت هذه هي المرة الأولى لفتح النشاط (وليس إعادة تدوير للشاشة)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new QuestsFragment())
                    .commit();
        }

        // 2. تعريف شريط التنقل السفلي وربطه بالـ ID
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // 3. برمجة المستمع لضغطات المستخدم على أيقونات التنقل
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            // التحقق من العنصر الذي تم الضغط عليه وتحديد الـ Fragment المناسب
            if (id == R.id.nav_quests) {
                selectedFragment = new QuestsFragment();
            } else if (id == R.id.nav_level) {
                selectedFragment = new LevelFragment();
            } else if (id == R.id.nav_rewards) {
                selectedFragment = new RewardsFragment();
            } else if (id == R.id.nav_competition) {
                selectedFragment = new CompetitionFragment();
            } else if (id == R.id.nav_settings) {
                selectedFragment = new Settings_Fragment();
            }

            // 4. تنفيذ عملية التبديل بين الشاشات (Fragment Transaction)
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // إضافة أنيميشن بسيط للانتقال
                        .commit();
                return true; // تعني أن الحدث تم التعامل معه بنجاح
            }
            return false;
        });
    }
}
