package com.example.reemafinal2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.MyTasksTable.MyQuest;
import com.example.reemafinal2.data.MyTasksTable.MyQuestAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuestsFragment extends Fragment {

    private ListView lstQuests;
    private MyQuestAdapter questAdapter;
    private DatabaseReference questsRef;
    private FloatingActionButton btnAddQuest;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quests, container, false);

        // 1. Initialize UI
        lstQuests = view.findViewById(R.id.lstQuestsFragment);
        btnAddQuest = view.findViewById(R.id.btnAddQuest);

        questAdapter = new MyQuestAdapter(getContext(), R.layout.quest_item_layout);
        lstQuests.setAdapter(questAdapter);

        // 2. Admin Check (Show/Hide button)
//        if(FirebaseAuth.getInstance().getCurrentUser() != null &&
//                "reema567@gmail.com".equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
//            btnAddQuest.setVisibility(View.VISIBLE);
//        } else {
//            btnAddQuest.setVisibility(View.GONE);
//        }

        // 3. Click Listener to open AddQuest activity
        btnAddQuest.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddQuest.class);
            startActivity(intent);
        });

        // 4. Load Data from Firebase
        questsRef = FirebaseDatabase.getInstance().getReference("quests");
        loadQuestsFromFirebase();

        return view;
    }

    private void loadQuestsFromFirebase() {
        questsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MyQuest> quests = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MyQuest quest = dataSnapshot.getValue(MyQuest.class);
                    if (quest != null) {
                        quests.add(quest);
                    }
                }
                questAdapter.clear();
                questAdapter.addAll(quests);
                questAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
