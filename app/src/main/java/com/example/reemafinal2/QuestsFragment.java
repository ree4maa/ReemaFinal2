package com.example.reemafinal2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.MyTasksTable.MyQuest;
import com.example.reemafinal2.data.MyTasksTable.MyQuestAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * QuestsFragment: كلاس مسؤول عن عرض قائمة المهام المتاحة للمستخدمين.
 * يتم جلب البيانات من Firebase وعرضها باستخدام Custom Adapter.
 */
public class QuestsFragment extends Fragment {

    // عناصر الواجهة
    private ListView lstQuests; // قائمة عرض المهام
    private MyQuestAdapter questAdapter; // المحول الخاص بالمهام
    private DatabaseReference questsRef; // مرجع قاعدة البيانات لفرع المهام
    private FloatingActionButton btnAddQuest; // الزر العائم لإضافة مهمة جديدة

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ربط ملف الواجهة fragment_quests
        View view = inflater.inflate(R.layout.fragment_quests, container, false);

        // 1. تعريف العناصر وربطها بالواجهة
        lstQuests = view.findViewById(R.id.lstQuestsFragment);
        btnAddQuest = view.findViewById(R.id.btnAddQuest);

        // 2. إعداد الـ Adapter (المحول) لربط البيانات بالـ ListView
        // تم استخدام تخطيط مخصص لكل عنصر (quest_item_layout)
        questAdapter = new MyQuestAdapter(getContext(), R.layout.quest_item_layout);
        lstQuests.setAdapter(questAdapter);

        /*
         * 3. فحص الصلاحيات (Admin Check):
         * الكود أدناه يقوم بإظهار زر الإضافة فقط إذا كان البريد الإلكتروني يخص مدير النظام.
         */
        // if(FirebaseAuth.getInstance().getCurrentUser() != null &&
        //    "reema567@gmail.com".equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
        //     btnAddQuest.setVisibility(View.VISIBLE);
        // } else {
        //     btnAddQuest.setVisibility(View.GONE);
        // }

        // 4. برمجة زر الإضافة لفتح شاشة AddQuest
        btnAddQuest.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddQuest.class);
            startActivity(intent);
        });

        // 5. تهيئة مرجع Firebase للوصول لجدول "quests"
        questsRef = FirebaseDatabase.getInstance().getReference("quests");

        // جلب البيانات من السيرفر
        loadQuestsFromFirebase();

        return view;
    }

    /**
     * دالة تقوم بمراقبة قاعدة البيانات وجلب المهام فور توفرها أو تحديثها.
     */
    private void loadQuestsFromFirebase() {
        // الاستماع للتغييرات في السيرفر بشكل حي
        questsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MyQuest> quests = new ArrayList<>();

                // المرور على جميع المهام القادمة من السيرفر
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MyQuest quest = dataSnapshot.getValue(MyQuest.class);
                    if (quest != null) {
                        quests.add(quest);
                    }
                }

                // تحديث القائمة في الواجهة
                questAdapter.clear(); // مسح البيانات القديمة لتجنب التكرار
                questAdapter.addAll(quests); // إضافة القائمة الجديدة
                questAdapter.notifyDataSetChanged(); // إبلاغ الـ ListView بالتحديث لعرض البيانات
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // يمكن إضافة معالجة للأخطاء هنا في حال فشل الاتصال بالسيرفر
            }
        });
    }
}