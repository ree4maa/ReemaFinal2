package com.example.reemafinal2.data.MyTasksTable;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MyQuestQuery {
    @Query("SELECT * FROM MyQuest order by rewardpoints DESC")
    List<MyQuest> getAllTasks();
    @Query("SELECT * FROM MyQuest WHERE userId = :userId")
    List<MyQuest> getTasksByUserId(String userId);

    @Query("SELECT * FROM MyQuest WHERE gameId = :gameId")
    List<MyQuest> getTasksByGameId(String gameId);

    @Query("SELECT * FROM MyQuest WHERE isCompleted = :isCompleted")
    List<MyQuest> getTasksByIsCompleted(boolean isCompleted);

    @Query("SELECT * FROM MyQuest WHERE title LIKE :title")
    List<MyQuest> getTasksByTitleContains(String title);

}

