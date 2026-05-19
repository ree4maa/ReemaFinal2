package com.example.reemafinal2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

/**
 * CompetitionFragment: الشاشة المسؤولة عن توجيه المستخدم لأنماط اللعب المختلفة.
 * تحتوي على خيارات اللعب الجماعي، العشوائي، والذكاء الاصطناعي.
 */
public class CompetitionFragment extends Fragment {

    // تعريف البطاقات التفاعلية (Material Design Cards)
    private MaterialCardView cardFriends;
    private MaterialCardView cardRandom;
    private MaterialCardView cardAI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ربط ملف الواجهة fragment_competition
        View view = inflater.inflate(R.layout.fragment_competition, container, false);

        // 1. تعريف المكونات وربطها بالـ IDs من ملف XML
        cardFriends = view.findViewById(R.id.cardFriends);
        cardRandom = view.findViewById(R.id.cardRandom);
        cardAI = view.findViewById(R.id.cardAI);

        // 2. برمجة زر "اللعب مع الأصدقاء"
        cardFriends.setOnClickListener(v -> {
            // حالياً تظهر رسالة تنبيه فقط (قيد التطوير)
            Toast.makeText(getActivity(), "Opening Friends List...", Toast.LENGTH_SHORT).show();
        });

        // 3. برمجة زر "المنافسة العشوائية"
        cardRandom.setOnClickListener(v -> {
            // حالياً تظهر رسالة بحث عن خصم (قيد التطوير)
            Toast.makeText(getActivity(), "Searching for Opponent...", Toast.LENGTH_SHORT).show();
        });

        // 4. برمجة زر "المساعد الذكي - AI" (ميزة مفعلة بالكامل)
        cardAI.setOnClickListener(v -> {
            // إنشاء نية (Intent) للانتقال لشاشة الذكاء الاصطناعي
            Intent intent = new Intent(getActivity(), Ai.class);
            startActivity(intent);

            // إضافة تأثير انتقالي ناعم (تلاشي) عند فتح الشاشة
            if (getActivity() != null) {
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return view;
    }
}