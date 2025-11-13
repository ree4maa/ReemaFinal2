package com.example.reemafinal2.data.MyTasksTable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
    public class MyTask {
    @PrimaryKey(autoGenerate = true)
    public long Keyid;
    public String importance;
    public String shortTitle;
    public String text;
    public String time;
    public String isCompleted;
    public String subjId;
    public String UserId;

    @NonNull
    @Override
    public String toString() {
        return "MyTask{" +
                "Keyid=" + Keyid +
                ", importance='" + importance + '\'' +
                ", shortTitle='" + shortTitle + '\'' +
                ", text='" + text + '\'' +
                ", time='" + time + '\'' +
                ", isCompleted='" + isCompleted + '\'' +
                ", subjId='" + subjId + '\'' +
                ", UserId='" + UserId + '\'' +
                '}';
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getIsCompleted() {
        return isCompleted;
    }
    public void setIsCompleted(String isCompleted) {
        this.isCompleted = isCompleted;
    }
    public String getSubjId() {
        return subjId;
    }
    public void setSubjId(String subjId) {
        this.subjId = subjId;
    }
    public String getUserId() {
        return UserId;
    }
    public void setUserId(String userId) {
        UserId = userId;
    }
}


