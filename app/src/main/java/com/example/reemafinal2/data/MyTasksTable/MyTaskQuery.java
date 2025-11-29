package com.example.reemafinal2.data.MyTasksTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyTaskQuery {
    @Query("SELECT * FROM MyTask order by rewardpoints DESC")
    List<MyTask> getAllTasks();
    @Query("SELECT * FROM MyTask WHERE userId = :userId")
    List<MyTask> getTasksByUserId(String userId);
    @Query("SELECT * FROM MyTask WHERE subjId = :subjId")
    List<MyTask> getTasksBySubjId(String subjId);
    @Query("SELECT * FROM MyTask WHERE gameId = :gameId")
    List<MyTask> getTasksByGameId(String gameId);
    @Query("SELECT * FROM MyTask WHERE noteId = :noteId")
    List<MyTask> getTasksByNoteId(String noteId);
    @Query("SELECT * FROM MyTask WHERE catagory = :catagory")
    List<MyTask> getTasksByCatagory(String catagory);
    @Query("SELECT * FROM MyTask WHERE isCompleted = :isCompleted")
    List<MyTask> getTasksByIsCompleted(boolean isCompleted);
    @Query("SELECT * FROM MyTask WHERE time = :time")
    List<MyTask> getTasksByTime(String time);
    @Query("SELECT * FROM MyTask WHERE description = :description")
    List<MyTask> getTasksByDescription(String description);
    @Query("SELECT * FROM MyTask WHERE title = :title")
    List<MyTask> getTasksByTitle(String title);
}

