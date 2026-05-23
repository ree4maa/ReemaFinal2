package com.example.reemafinal2.data.MyTasksTable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity
//•أنت تعطي "تصريحاً" للأندرويد بأن هذا الكائن يمكن تحويله إلى مجموعة من البايتات (Bytes) ليتم نقله عبر الـ Intent.
// •هذا ما سمح لسطر الكود intent.putExtra("QUEST_DATA", current); بأن يعمل بدون خطأ برمجياً.
public class MyQuest implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long keyId;
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
                "Keyid=" + keyId +
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

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
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

//عندما تنتقل من شاشة إلى أخرى وتريد إرسال "نص" أو "رقم"، الأمر سهل لأن الأندرويد يعرف كيف يتعامل معها. لكنك هنا تحاول
// إرسال كائن كامل (Object) وهو current (الذي هو من نوع MyQuest).الأندرويد لا يعرف كيف "يفكك" هذا الكائن ليحوله
// إلى بيانات ويرسلها عبر الـ Intent ثم يعيد تجميعه في الشاشة التالية، إلا إذا أخبرته أن هذا الكلاس "قابل للتسلسل" (Serializable).
