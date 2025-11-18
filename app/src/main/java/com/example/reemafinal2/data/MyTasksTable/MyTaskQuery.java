package com.example.reemafinal2.data.MyTasksTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyTaskQuery {
    @Query("SELECT * FROM MyTask order by imp DESC")
    List<MyTask> getAll();
}
