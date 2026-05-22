package com.example.reemafinal2.data.MyTasksTable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.Serializable;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.reemafinal2.AddQuest;
import com.example.reemafinal2.PlayQuestActivity;
import com.example.reemafinal2.R;
import com.example.reemafinal2.data.AppDatabase;
import com.google.firebase.database.FirebaseDatabase;

/**
 * <h2>MyQuestAdapter</h2>
 * <p>
 * محول مخصص (Custom Adapter) لربط قائمة المهام (Quests) بـ ListView.
 * مسؤول عن عرض بيانات المهمة، وإدارة عمليات الحذف، التعديل، وبدء المهمة.
 * </p>
 *
 * <p>
 * This custom adapter binds a list of {@link MyQuest} objects to a ListView.
 * It handles UI binding, deletion (Firebase/Room), editing, and starting missions.
 * </p>
 *
 * @author Reema
 * @version 2.0
 */
public class MyQuestAdapter extends ArrayAdapter<MyQuest> {

    /** معرف ملف التصميم لكل صف في القائمة */
    private final int itemLayout;

    /**
     * constructor لتهيئة المحول.
     *
     * @param context سياق التطبيق الحالي.
     * @param resource معرف ملف XML الخاص بتصميم العنصر (quest_item_layout).
     */
    public MyQuestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.itemLayout = resource;
    }

    /**
     * تقوم هذه الدالة بإنشاء وتعبئة واجهة المستخدم لكل عنصر في القائمة.
     *
     * @param position موقع العنصر في القائمة.
     * @param convertView الواجهة القديمة لإعادة الاستخدام (إن وجدت).
     * @param parent الحاوية الأب التي ستحوي هذه الواجهة.
     * @return واجهة مستخدم مكتملة البيانات (View).
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View vitem = convertView;

        // تضخيم الواجهة إذا لم تكن موجودة مسبقاً
        if (vitem == null) {
            vitem = LayoutInflater.from(getContext()).inflate(itemLayout, parent, false);
        }

        // 1. ربط عناصر واجهة المستخدم بالـ IDs
        TextView tvtitle = vitem.findViewById(R.id.TV_taskType);
        TextView TV_time = vitem.findViewById(R.id.TV_time);
        TextView TV_score = vitem.findViewById(R.id.TV_score);
        TextView TV_gameId = vitem.findViewById(R.id.TV_gameId);
        TextView TV_challenges = vitem.findViewById(R.id.TV_challenges_list);
        Button btnstart = vitem.findViewById(R.id.btnStart);

        // أزرار التحكم المودرن (تعديل وحذف)
        View btnDelete = vitem.findViewById(R.id.btnDeleteQuest);
        View btnEdit = vitem.findViewById(R.id.btnEditQuest);

        // 2. جلب كائن المهمة الحالي بناءً على موقعه
        MyQuest current = getItem(position);

        if (current != null) {
            // تعبئة النصوص مع فحص الحماية من القيم الفارغة
            tvtitle.setText(current.getTitle() != null ? current.getTitle() : "Untitled Quest");
            TV_time.setText(current.getTime() != null ? current.getTime() : "N/A");
            TV_gameId.setText("ID: " + (current.getGameId() != null ? current.getGameId() : "---"));
            TV_score.setText(String.valueOf(current.getRewardpoints()) + " Pts");

            if (current.getNote() != null && !current.getNote().isEmpty()) {
                TV_challenges.setText(current.getNote());
            } else {
                TV_challenges.setText("• Complete the mission to win!");
            }

            /**
             * برمجة زر البدء (START):
             * يفتح شاشة تنفيذ المهمة PlayQuestActivity.
             */
            btnstart.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), PlayQuestActivity.class);
                intent.putExtra("QUEST_TITLE", current.getTitle());
                getContext().startActivity(intent);
            });

            /**
             * برمجة زر الحذف (DELETE):
             * يظهر نافذة تأكيد، ثم يحذف المهمة من Firebase و Room Database.
             */
            // ابحث عن برمجة زر الحذف واستبدلها بهذا الكود المضمون:

            btnDelete.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(getContext())
                        .setTitle("Delete Mission")
                        .setMessage("Are you sure you want to delete this mission permanently?")
                        .setPositiveButton("Delete", (dialog, which) -> {

                            // 1. الحذف من Firebase (فقط إذا كان المعرف موجوداً)
                            String uId = current.getUserId();
                            if (uId != null && !uId.isEmpty()) {
                                FirebaseDatabase.getInstance().getReference("quests")
                                        .child(uId) // هنا لن يحدث الخطأ لأننا فحصنا القيمة
                                        .removeValue();
                            } else {
                                // إذا كان الـ ID غير موجود في الكائن، نحاول حذفه من القائمة مباشرة
                                android.util.Log.e("MyQuestAdapter", "UserId is null, skipping Firebase delete");
                            }

                            // 2. الحذف من Room (يجب أن يتم دائماً لتحديث الواجهة)
                            new Thread(() -> {
                                try {
                                    // الحذف من Room باستخدام المفتاح المحلي (PrimaryKey)
                                    AppDatabase.getDp(getContext()).myTaskQuery().delete(current);

                                    // العودة لخيط الواجهة لتحديث الـ ListView
                                    if (getContext() instanceof Activity) {
                                        ((Activity) getContext()).runOnUiThread(() -> {
                                            remove(current); // إزالة من القائمة
                                            notifyDataSetChanged();
                                            Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            /**
             * برمجة زر التعديل (EDIT):
             * يفتح شاشة AddQuest ويمرر كائن المهمة الحالي لتعديل بياناته.
             */
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddQuest.class);
                intent.putExtra("EDIT_MODE", true);
                intent.putExtra("QUEST_DATA", current);
                getContext().startActivity(intent);
            });
        }

        return vitem;
    }
}