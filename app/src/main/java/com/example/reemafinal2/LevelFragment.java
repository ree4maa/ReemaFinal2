package com.example.reemafinal2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.AppDatabase;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

public class LevelFragment extends Fragment {

    private ShapeableImageView imgProfile;
    private TextView tvPlayerName, tvCurrentLevel, tvXpPercentage;
    private ProgressBar xpProgressBar;

    // Firebase
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    // Local Database (Room)
    private AppDatabase localDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_level, container, false);

        // 1. Initialize UI Views
        imgProfile = view.findViewById(R.id.imgProfile);
        tvPlayerName = view.findViewById(R.id.tvPlayerName);
        tvCurrentLevel = view.findViewById(R.id.tvCurrentLevel);
        tvXpPercentage = view.findViewById(R.id.tvXpPercentage);
        xpProgressBar = view.findViewById(R.id.xpProgressBar);

        // 2. Initialize Databases
        mAuth = FirebaseAuth.getInstance();
        localDb = AppDatabase.getDp(getContext()); // Ensure your AppDatabase has this method

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Load from Firebase and Sync to Local
            loadAndSyncData();
        } else {
            loadLocalDataOnly(); // If not logged in, show local guest data
        }

        return view;
    }

    private void loadAndSyncData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    Long level = snapshot.child("level").getValue(Long.class);
                    Long xp = snapshot.child("xp").getValue(Long.class);
                    String profilePicUrl = snapshot.child("profilePic").getValue(String.class);

                    // Update UI immediately from Firebase
                    updateUI(name, level, xp, profilePicUrl);

                    // Sync to Local Room Database in background
                    syncToLocal(name, level, xp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadLocalDataOnly(); // If Firebase fails, use local data
            }
        });
    }

    private void updateUI(String name, Long level, Long xp, String profilePicUrl) {
        tvPlayerName.setText(name != null ? name : "Player");
        tvCurrentLevel.setText(String.valueOf(level != null ? level : 1));

        int currentXp = (xp != null) ? xp.intValue() : 0;
        xpProgressBar.setMax(100); // Or your max level XP
        xpProgressBar.setProgress(currentXp);
        tvXpPercentage.setText(currentXp + " / 100");

        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            Picasso.get().load(profilePicUrl).placeholder(android.R.drawable.ic_menu_gallery).into(imgProfile);
        }
    }

    private void syncToLocal(String name, Long level, Long xp) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Create a User object compatible with your Room Entity
            // Example: MyUser user = new MyUser(name, level.intValue(), xp.intValue());
            // localDb.userDao().insert(user);
        });
    }

    private void loadLocalDataOnly() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Fetch from Room
            // MyUser localUser = localDb.userDao().getUser();
            // getActivity().runOnUiThread(() -> updateUI(localUser.name, ...));
        });
    }

    private void updateXPUI(int currentXp, int maxXp) {
        xpProgressBar.setMax(maxXp);
        xpProgressBar.setProgress(currentXp);
        tvXpPercentage.setText(currentXp + " / " + maxXp);
    }
}