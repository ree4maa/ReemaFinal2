package com.example.reemafinal2.data.MyUser;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao//لتحديد ان الواجهخة تحوي استعلامات على قاعدة بايانات
public interface MyPlayerQuery
{   //استخراج جميع المستعملين
    @Query("SELECT * FROM MyPlayer")
    List<MyPlayer> getAll();
    // استخراج مستعمل حسب رقم المميز لهid
    @Query("SELECT * FROM MyPlayer WHERE keyid IN (:userIds)")
    List<MyPlayer> loadAllByIds(int[] userIds);
    //هل المستعمل موجود حسب الايميل وكلمة السر
    @Query("SELECT * FROM MyPlayer WHERE email = :myEmail AND password = :myPassword LIMIT 1")
    MyPlayer checkEmailPassword(String myEmail, String myPassword);
    //فحص هل الايميل موجود من قبل
    @Query("SELECT * FROM MyPlayer WHERE email = :myEmail LIMIT 1")
    MyPlayer checkEmail(String myEmail);
    @Insert
// اضافة مستعمل او مجموعة مستعملين
    void insertAll(MyPlayer... users);
    @Delete
// حذف
    void delete(MyPlayer user);
    //حذف حسب الرقم المميز id
    @Query("Delete From MyPlayer WHERE keyid=:id ")
    void delete(int id);
    @Insert//اضافة مستعمل واحد
    void insert(MyPlayer myPlayer);
    @Update
//تعديل مستعمل او قائمة مستعملين
    void update(MyPlayer...values);
}


