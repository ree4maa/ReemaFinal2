package com.example.reemafinal2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ReportProblemFragment extends Fragment {

    private EditText etProblemDescription;
    private MaterialButton btnSubmitReport;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_problem, container, false);

        etProblemDescription = view.findViewById(R.id.etProblemDescription);
        btnSubmitReport = view.findViewById(R.id.btnSubmitReport);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btnSubmitReport.setOnClickListener(v -> {
            String message = etProblemDescription.getText().toString().trim();
            if (!message.isEmpty()) {
                submitReport(message);
            } else {
                Toast.makeText(getContext(), "Please describe the problem", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void submitReport(String message) {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "anonymous";
        String reportId = mDatabase.child("reports").push().getKey();

        Map<String, Object> report = new HashMap<>();
        report.put("userId", userId);
        report.put("message", message);
        report.put("timestamp", System.currentTimeMillis());

        if (reportId != null) {
            mDatabase.child("reports").child(reportId).setValue(report)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Problem Reported Successfully!", Toast.LENGTH_SHORT).show();
                        // Navigate back to the previous fragment
                        getParentFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to send report", Toast.LENGTH_SHORT).show());
        }
    }
}
