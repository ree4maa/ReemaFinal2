package com.example.reemafinal2.data.MyTasksTable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reemafinal2.PlayQuestActivity;
import com.example.reemafinal2.R;

/**
 * MyQuestAdapter: المحول المسؤول عن عرض بيانات كل مهمة داخل قائمة ListView.
 * يقوم بربط كائنات MyQuest بالتصميم المرئي الخاص بها.
 */
public class MyQuestAdapter extends ArrayAdapter<MyQuest> {

    private final int itemLayout; // معرف ملف XML الخاص بتصميم العنصر

    /**
     * constructor لتهيئة المحول.
     * @param context سياق التطبيق.
     * @param resource ملف XML الخاص بتصميم الصف الواحد (quest_item_layout).
     */
    public MyQuestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout = resource;
    }

    /**
     * الدالة المسؤولة عن بناء وتعبئة واجهة كل عنصر في القائمة.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View vitem = convertView;

        // 1. استخدام نظام إعادة تدوير الصفوف (Recycling) لتحسين الأداء
        if (vitem == null) {
            vitem = LayoutInflater.from(getContext()).inflate(itemLayout, parent, false);
        }

        // 2. تعريف عناصر واجهة المستخدم داخل الصف الواحد
        TextView tvtitle = vitem.findViewById(R.id.TV_taskType);
        TextView TV_time = vitem.findViewById(R.id.TV_time);
        TextView TV_score = vitem.findViewById(R.id.TV_score);
        TextView TV_gameId = vitem.findViewById(R.id.TV_gameId);
        TextView TV_challenges = vitem.findViewById(R.id.TV_challenges_list);
        Button btnstart = vitem.findViewById(R.id.btnStart);

        // 3. جلب بيانات المهمة الحالية بناءً على موقعها في القائمة
        MyQuest current = getItem(position);

        if (current != null) {
            // تعبئة النصوص مع فحص الحماية (Safety Checks) لمنع الـ NullPointerException
            tvtitle.setText(current.getTitle() != null ? current.getTitle() : "Untitled Quest");
            TV_time.setText(current.getTime() != null ? current.getTime() : "N/A");
            TV_gameId.setText("ID: " + (current.getGameId() != null ? current.getGameId() : "---"));

            // تحويل النقاط (int) إلى نص (String) لضمان عدم حدوث خطأ برمجي أثناء العرض
            TV_score.setText(String.valueOf(current.getRewardpoints()));

            // عرض الملاحظات أو عرض نص افتراضي في حال كانت فارغة
            if (current.getNote() != null && !current.getNote().isEmpty()) {
                TV_challenges.setText(current.getNote());
            } else {
                TV_challenges.setText("• Complete the mission to win!");
            }

            // 4. برمجة زر البدء (Start) لفتح شاشة تنفيذ المهمة
            btnstart.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), PlayQuestActivity.class);
                // تمرير عنوان المهمة للشاشة القادمة
                intent.putExtra("QUEST_TITLE", current.getTitle());
                getContext().startActivity(intent);
            });
        }

        return vitem;
    }
}