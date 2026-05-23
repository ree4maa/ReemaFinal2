package com.example.reemafinal2.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.reemafinal2.data.MyTasksTable.MyQuest;
import com.example.reemafinal2.data.MyTasksTable.MyQuestQuery;
import com.example.reemafinal2.data.MyUser.MyPlayer;
import com.example.reemafinal2.data.MyUser.MyPlayerQuery;

/**
 * AppDatabase: الكلاس المسؤول عن إعداد وإدارة قاعدة بيانات Room المحلية.
 * يجمع بين جداول المستخدمين والمهام ويوفر طرق الوصول إليها.
 */
@Database(entities = {MyPlayer.class, MyQuest.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {

    // نسخة ثابتة واحدة من قاعدة البيانات (Singleton Instance)
    private static AppDatabase dp;

    /**
     * الوصول إلى استعلامات جدول المستخدمين.
     */
    public abstract MyPlayerQuery myUserQuery();

    /**
     * الوصول إلى استعلامات جدول المهام.
     */
    public abstract MyQuestQuery myTaskQuery();

    /**
     * دالة ثابتة للحصول على نسخة قاعدة البيانات.
     *
     * @param context سياق التطبيق للوصول إلى الملفات.
     * @return نسخة مفعلة من AppDatabase.
     */
    public static AppDatabase getDp(Context context) {
        if (dp == null) {
            // بناء قاعدة البيانات إذا لم تكن موجودة
            dp = Room.databaseBuilder(context, AppDatabase.class, "reemaDatabase")
                    // مسح البيانات القديمة عند تغيير الإصدار (Migration)
                    .fallbackToDestructiveMigration()
                    // السماح بالاستعلامات المباشرة (لسرعة التطوير)
                    .allowMainThreadQueries()
                    .build();
        }
        return dp;
    }
}
