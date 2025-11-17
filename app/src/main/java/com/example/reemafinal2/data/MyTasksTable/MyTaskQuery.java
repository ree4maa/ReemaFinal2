package com.example.reemafinal2.data.MyTasksTable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyTaskQuery {
@Query("SELECT * FROM MyTask")
List<MyTask> getAll();
@Query("SELECT * FROM MyTask WHERE keyid IN (:userIds)")
List<MyTask> loadAllByIds(int[] userIds);
@Query("SELECT * FROM MyTask WHERE title LIKE :name")
List<MyTask> findByName(String name);
@Insert
void insertAll(MyTask... users);
@Delete
void delete(MyTask user);
@Update
void update(MyTask... users);
@Query("Delete From MyTask WHERE keyid=:id ")
void delete(int id);

List<MyTaskQuery> getTaskByUserId(String userId);
}
