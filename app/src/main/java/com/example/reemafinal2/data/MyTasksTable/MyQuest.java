package com.example.reemafinal2.data.MyTasksTable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
    public class MyQuest {
    @PrimaryKey(autoGenerate = true)
    public long Keyid;
  public String title;
  public String description;
  public String time;
  public boolean isCompleted;
  public String userId;
  public String subjId;
  public String gameId;
  public String noteId;
  public String catagory;
  public int rewardpoints;

    @NonNull
    @Override
    public String toString() {
        return "MyTask{" +
                "Keyid=" + Keyid +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", isCompleted=" + isCompleted +
                ", userId='" + userId + '\'' +
                ", subjId='" + subjId + '\'' +
                ", gameId='" + gameId + '\'' +
                ", noteId='" + noteId + '\'' +
                ", catagory='" + catagory + '\'' +
                ", rewardpoints=" + rewardpoints +
                '}';
    }

    public long getKeyid() {
        return Keyid;
    }

    public void setKeyid(long keyid) {
        Keyid = keyid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getSubjId() {
        return subjId;
    }

    public void setSubjId(String subjId) {
        this.subjId = subjId;
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

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public int getRewardpoints() {
        return rewardpoints;
    }

    public void setRewardpoints(int rewardpoints) {
        this.rewardpoints = rewardpoints;
    }
}


