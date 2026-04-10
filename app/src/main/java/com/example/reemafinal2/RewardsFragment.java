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
import java.util.concurrent.Executors; // For Local Database threading

public class RewardsFragment extends Fragment {

    private TextView tvTotalRewards, tvEarnedPoints, tvFriendGifts, tvMyRank, tvGlobalGoalStatus;
    private LinearLayout leaderboardContainer;
    private ProgressBar globalProgressBar;

    // Cloud Database (Firebase)
    private DatabaseReference dbRef;
    private DatabaseReference globalRef;
    private String currentUserId;

    // Local Database (Room)
    private AppDatabase localDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        // 1. Initialize UI
        tvTotalRewards = view.findViewById(R.id.tvTotalRewards);
        tvEarnedPoints = view.findViewById(R.id.tvEarnedPoints);
        tvFriendGifts = view.findViewById(R.id.tvFriendGifts);
        tvMyRank = view.findViewById(R.id.tvMyRank);
        tvGlobalGoalStatus = view.findViewById(R.id.tvGlobalGoalStatus);
        globalProgressBar = view.findViewById(R.id.globalProgressBar);
        leaderboardContainer = view.findViewById(R.id.leaderboardContainer);

        // 2. Initialize Firebase (Cloud Database)
        currentUserId = FirebaseAuth.getInstance().getUid();
        dbRef = FirebaseDatabase.getInstance().getReference("users");
        globalRef = FirebaseDatabase.getInstance().getReference("global_stats");

        // 3. Initialize Room (Local Database)
        localDb = AppDatabase.getDp(getContext());

        // 4. Load Data
        loadGlobalLeaderboard();
        loadGlobalCommunityGoal();

        return view;
    }

    private void loadGlobalCommunityGoal() {
        globalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Long communityTotal = snapshot.child("communityTotal").getValue(Long.class);
                    Long goal = snapshot.child("currentGoal").getValue(Long.class);

                    if (communityTotal == null) communityTotal = 0L;
                    if (goal == null || goal == 0) goal = 50000L;

                    tvGlobalGoalStatus.setText("Goal: " + String.format("%,d", communityTotal) + " / " + String.format("%,d", goal));
                    int progress = (int) ((communityTotal * 100) / goal);
                    globalProgressBar.setProgress(progress);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadGlobalLeaderboard() {
        dbRef.orderByChild("totalRewards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DataSnapshot> players = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    players.add(userSnap);
                }
                Collections.reverse(players);

                leaderboardContainer.removeAllViews();
                int rank = 1;

                for (DataSnapshot player : players) {
                    String name = player.child("name").getValue(String.class);
                    Long total = player.child("totalRewards").getValue(Long.class);
                    Long earned = player.child("playRewards").getValue(Long.class);
                    Long friends = player.child("friendRewards").getValue(Long.class);

                    if (total == null) total = 0L;
                    if (earned == null) earned = 0L;
                    if (friends == null) friends = 0L;

                    if (player.getKey() != null && player.getKey().equals(currentUserId)) {
                        // Update UI
                        tvTotalRewards.setText(String.format("%,d", total));
                        tvEarnedPoints.setText(earned + " pts");
                        tvFriendGifts.setText(friends + " pts");
                        tvMyRank.setText("RANK #" + rank);

                        // SYNC TO LOCAL DATABASE (Room)
                        saveToLocalDatabase(total, earned, friends);
                    }

                    if (rank <= 5) {
                        addLeaderboardRow(rank, name, total);
                    }
                    rank++;
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // --- Local Database Logic ---
    private void saveToLocalDatabase(Long total, Long earned, Long friends) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // This part depends on your specific Room Entity (User or Reward)
            // Example: localDb.userDao().updateRewards(currentUserId, total, earned, friends);
        });
    }

    private void addLeaderboardRow(int rank, String name, long score) {
        View row = getLayoutInflater().inflate(R.layout.item_leaderboard, null);
        ((TextView)row.findViewById(R.id.tvRankNum)).setText(String.valueOf(rank));
        ((TextView)row.findViewById(R.id.tvPlayerName)).setText(name);
        ((TextView)row.findViewById(R.id.tvPlayerScore)).setText(score + " pts");
        leaderboardContainer.addView(row);
    }
}