package com.example.reemafinal2.data.MyTasksTable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
    public class MyQuest {
    @PrimaryKey(autoGenerate = true)
    public long Keyid;
  public String title;
  public String time;
  public boolean isCompleted;
  public String userId;
  public String subject;
  public String gameId;
  public String note;
  public int rewardpoints;

    @NonNull
    @Override
    public String toString() {
        return "MyTask{" +
                "Keyid=" + Keyid +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", isCompleted=" + isCompleted +
                ", userId='" + userId + '\'' +
                ", subjId='" + subject + '\'' +
                ", gameId='" + gameId + '\'' +
                ", noteId='" + note + '\'' +
                ", rewardpoints=" + rewardpoints +
                '}';
    }

    public long getKeyid() {
        return Keyid;
    }

    public void setKeyid(long keyid) {
        Keyid = keyid;
    }


    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subjId) {
        this.subject = subjId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getRewardpoints() {
        return rewardpoints;
    }

    public void setRewardpoints(int rewardpoints) {
        this.rewardpoints = rewardpoints;
    }
}


