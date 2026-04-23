package com.example.reemafinal2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.AppDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class Settings_Fragment extends Fragment {

    private TextView tvUserEmail;
    private View btnHelp, btnReportProblem, btnLogout;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Local Database (Room)
    private AppDatabase localDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_, container, false);

        // 1. Initialize UI Elements
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnHelp = view.findViewById(R.id.btnHelp);
        btnReportProblem = view.findViewById(R.id.btnReportProblem);
        btnLogout = view.findViewById(R.id.btnLogout);

        // 2. Initialize Firebase & Room
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        localDb = AppDatabase.getDp(getContext());

        // 3. Display Current User Email
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            tvUserEmail.setText(currentUser.getEmail());
        }

        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        // --- HELP CENTER ---
        btnHelp.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Help Center: Contact support@reema.com", Toast.LENGTH_LONG).show();
        });

        // --- REPORT A PROBLEM (Firebase Database) ---
        btnReportProblem.setOnClickListener(v -> {
            reportProblemToFirebase("User reported a bug from settings.");
        });

        // --- LOGOUT (Firebase + Room) ---
        btnLogout.setOnClickListener(v -> {
            performLogout();
        });
    }

    private void reportProblemToFirebase(String message) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";
        String reportId = mDatabase.child("reports").push().getKey();

        Map<String, Object> report = new HashMap<>();
        report.put("userId", userId);
        report.put("message", message);
        report.put("timestamp", System.currentTimeMillis());

        if (reportId != null) {
            mDatabase.child("reports").child(reportId).setValue(report)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Problem Reported!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to send report", Toast.LENGTH_SHORT).show());
        }
    }

    private void performLogout() {
        // 1. Clear Local Room Database in a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            localDb.clearAllTables(); // This clears your local Room cache

            // 2. Sign out from Firebase on the main thread
            getActivity().runOnUiThread(() -> {
                mAuth.signOut();
                Toast.makeText(getContext(), "Logged Out Successfully", Toast.LENGTH_SHORT).show();

                // 3. Redirect to Login Activity (Change LoginActivity.class to your actual login class name)
                // Intent intent = new Intent(getActivity(), LoginActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // startActivity(intent);
            });
        });
    }
}