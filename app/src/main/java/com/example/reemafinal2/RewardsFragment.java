package com.example.reemafinal2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.AppDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * RewardsFragment: الكلاس المسؤول عن عرض نظام الجوائز ولوحة الصدارة والهدف العالمي.
 */
public class RewardsFragment extends Fragment {

    // عناصر واجهة المستخدم لعرض النقاط والرتبة
    private TextView tvTotalRewards, tvEarnedPoints, tvFriendGifts, tvMyRank, tvGlobalGoalStatus;
    private LinearLayout leaderboardContainer; // الحاوية التي ستعرض صفوف لوحة الصدارة
    private ProgressBar globalProgressBar; // شريط التقدم للهدف المجتمعي

    // مراجع قاعدة بيانات Firebase
    private DatabaseReference dbRef; // مرجع المستخدمين
    private DatabaseReference globalRef; // مرجع الإحصائيات العالمية
    private String currentUserId;

    // مرجع قاعدة البيانات المحلية
    private AppDatabase localDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // ربط ملف الـ XML الخاص بالفرجمينت
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        // 1. ربط العناصر البرمجية بالـ IDs في الـ XML
        tvTotalRewards = view.findViewById(R.id.tvTotalRewards);
        tvEarnedPoints = view.findViewById(R.id.tvEarnedPoints);
        tvFriendGifts = view.findViewById(R.id.tvFriendGifts);
        tvMyRank = view.findViewById(R.id.tvMyRank);
        tvGlobalGoalStatus = view.findViewById(R.id.tvGlobalGoalStatus);
        globalProgressBar = view.findViewById(R.id.globalProgressBar);
        leaderboardContainer = view.findViewById(R.id.leaderboardContainer);

        // 2. تهيئة الاتصال بـ Firebase
        currentUserId = FirebaseAuth.getInstance().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        globalRef = FirebaseDatabase.getInstance().getReference("global_stats");

        // 3. تهيئة قاعدة بيانات Room المحلية
        localDb = AppDatabase.getDp(getContext());

        // 4. البدء بجلب البيانات من السيرفر
        loadGlobalLeaderboard(); // جلب لوحة الصدارة
        loadGlobalCommunityGoal(); // جلب هدف المجتمع

        return view;
    }

    /**
     * جلب بيانات الهدف المشترك لجميع مستخدمي التطبيق من Firebase.
     */
    private void loadGlobalCommunityGoal() {
        globalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long communityTotal = snapshot.child("communityTotal").getValue(Long.class);
                    Long goal = snapshot.child("currentGoal").getValue(Long.class);

                    // قيم افتراضية في حال كانت البيانات فارغة
                    if (communityTotal == null) communityTotal = 0L;
                    if (goal == null || goal == 0) goal = 50000L;

                    // تحديث النص وشريط التقدم
                    tvGlobalGoalStatus.setText("Goal: " + String.format("%,d", communityTotal) + " / " + String.format("%,d", goal));
                    int progress = (int) ((communityTotal * 100) / goal);
                    globalProgressBar.setProgress(progress);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * جلب قائمة المستخدمين وترتيبهم حسب النقاط لعرض "أفضل 5" وتحديد رتبة المستخدم الحالي.
     */
    private void loadGlobalLeaderboard() {
        // ترتيب المستخدمين حسب حقل totalRewards
        dbRef.orderByChild("totalRewards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DataSnapshot> players = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    players.add(userSnap);
                }
                // عكس القائمة لأن الترتيب يكون تصاعدياً ونحن نريده تنازلياً
                Collections.reverse(players);

                leaderboardContainer.removeAllViews(); // مسح القائمة القديمة قبل التحديث
                int rank = 1;

                for (DataSnapshot player : players) {
                    // جلب بيانات اللاعب الحالي في الحلقة (Loop)
                    String name = player.child("name").getValue(String.class);
                    Long total = player.child("totalRewards").getValue(Long.class);
                    Long earned = player.child("playRewards").getValue(Long.class);
                    Long friends = player.child("friendRewards").getValue(Long.class);

                    if (total == null) total = 0L;

                    // إذا كان هذا اللاعب هو المستخدم الحالي للجهاز
                    if (player.getKey() != null && player.getKey().equals(currentUserId)) {
                        // تحديث واجهة المستخدم الخاصة بنقاطي
                        tvTotalRewards.setText(String.format("%,d", total));
                        tvEarnedPoints.setText(earned + " pts");
                        tvFriendGifts.setText(friends + " pts");
                        tvMyRank.setText("RANK #" + rank);

                        // مزامنة البيانات مع قاعدة البيانات المحلية Room
                        saveToLocalDatabase(total, earned, friends);
                    }

                    // عرض أفضل 5 لاعبين فقط في لوحة الصدارة
                    if (rank <= 5) {
                        addLeaderboardRow(rank, name, total);
                    }
                    rank++;
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * حفظ المكافآت محلياً لضمان توفرها للمستخدم في حال انقطاع الإنترنت.
     */
    private void saveToLocalDatabase(Long total, Long earned, Long friends) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // يتم استدعاء دالة التحديث من الـ DAO الخاص بـ Room هنا
            // مثال: localDb.userDao().updateRewards(currentUserId, total, earned, friends);
        });
    }

    /**
     * إضافة صف جديد بشكل ديناميكي داخل حاوية لوحة الصدارة.
     */
    private void addLeaderboardRow(int rank, String name, long score) {
        View row = getLayoutInflater().inflate(R.layout.item_leaderboard, null);
        ((TextView)row.findViewById(R.id.tvRankNum)).setText(String.valueOf(rank));
        ((TextView)row.findViewById(R.id.tvPlayerName)).setText(name);
        ((TextView)row.findViewById(R.id.tvPlayerScore)).setText(score + " pts");
        leaderboardContainer.addView(row);
    }
}