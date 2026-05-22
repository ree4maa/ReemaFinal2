package com.example.reemafinal2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.AppDatabase;
import com.example.reemafinal2.data.MyTasksTable.MyQuest;
import com.example.reemafinal2.data.MyTasksTable.MyQuestAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * <h2>QuestsFragment</h2>
 * <p>
 * هذا الكلاس مسؤول عن إدارة وعرض قائمة المهام (Quests) في التطبيق.
 * يعتمد على {@link MyQuestAdapter} لعرض العناصر بشكل مودرن، ويقوم بجلب البيانات
 * مباشرة من Firebase Realtime Database لضمان التزامن الحي.
 * </p>
 *
 * @author Reema
 * @version 2.0
 */
public class QuestsFragment extends Fragment {

    /** قائمة عرض المهام */
    private ListView lstQuests;
    /** المحول المخصص لربط البيانات بالواجهة */
    private MyQuestAdapter questAdapter;
    /** مرجع قاعدة بيانات Firebase */
    private DatabaseReference questsRef;
    /** الزر العائم لإضافة مهمة جديدة */
    private FloatingActionButton btnAddQuest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ربط الواجهة الخاصة بـ Fragment
        View view = inflater.inflate(R.layout.fragment_quests, container, false);

        // 1. ربط عناصر الواجهة بالـ IDs
        lstQuests = view.findViewById(R.id.lstQuestsFragment);
        btnAddQuest = view.findViewById(R.id.btnAddQuest);

        // 2. تهيئة الـ Adapter وتعيينه للـ ListView
        questAdapter = new MyQuestAdapter(getContext(), R.layout.quest_item_layout);
        lstQuests.setAdapter(questAdapter);

        // 3. برمجة زر الإضافة لفتح شاشة AddQuest
        btnAddQuest.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddQuest.class);
            startActivity(intent);
        });

        // 4. تهيئة مرجع قاعدة البيانات لجدول "quests"
        questsRef = FirebaseDatabase.getInstance().getReference("quests");

        return view;
    }
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

    /**
     * يتم استدعاء هذه الدالة عند ظهور الـ Fragment للمستخدم.
     * نبدأ جلب البيانات هنا لضمان تحديث القائمة عند العودة من شاشة الإضافة أو التعديل.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadQuestsFromFirebase();
    }

    /**
     * دالة جلب المهام من Firebase.
     * تتميز باستخدام ValueEventListener الذي يقوم بتحديث الواجهة تلقائياً
     * عند إضافة، حذف، أو تعديل أي مهمة في السيرفر.
     */
    private void loadQuestsFromFirebase() {
        questsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MyQuest> quests = new ArrayList<>();

                // سحب البيانات من السيرفر وتحويلها إلى كائنات MyQuest
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MyQuest quest = dataSnapshot.getValue(MyQuest.class);
                    if (quest != null) {
                        quests.add(quest);
                        // مزامنة البيانات مع Room في الخلفية
                        new Thread(() -> {
                            try {
                                // نقوم بإدخالها، وإذا كانت موجودة مسبقاً سيتم تجاهلها أو تحديثها
                                AppDatabase.getDp(getContext()).myTaskQuery().insertMyQuest(quest);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    }

                // تحديث الـ Adapter بالبيانات الجديدة
                if (isAdded()) { // التأكد من أن الـ Fragment لا يزال نشطاً لتجنب الـ Crash
                    questAdapter.clear();
                    questAdapter.addAll(quests);
                    questAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // في حال وجود خطأ في جلب البيانات من السيرفر
            }
        });
    }
}

