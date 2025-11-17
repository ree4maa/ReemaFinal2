package com.example.reemafinal2.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.reemafinal2.data.MyTasksTable.MyTask;
import com.example.reemafinal2.data.MyTasksTable.MyTaskQuery;
import com.example.reemafinal2.data.MyUser.MyUser;
import com.example.reemafinal2.data.MyUser.MyUserQuery;

public class AppDatabase {
    @Database(entities = {MyUser.class, MyTask.class,}, version = 1)
    public abstract class AppDatabase extends RoomDatabase {
        private static AppDatabase dp;
        public abstract MyUserQuery myUserQuery();
        public abstract MyTaskQuery myTaskQuery();
        public static AppDatabase getDp(Context context) {
            if (dp == null) {
                dp = Room.databaseBuilder(context, AppDatabase.class, "reemaDatabase")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build();
            }
            return dp;
        }
    }
}
