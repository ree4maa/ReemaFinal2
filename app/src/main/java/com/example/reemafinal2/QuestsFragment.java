package com.example.reemafinal2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.MyTasksTable.MyQuest;
import com.example.reemafinal2.data.MyTasksTable.MyQuestAdapter;
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

    public QuestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quests, container, false);

        // Initialize ListView and Adapter
        lstQuests = view.findViewById(R.id.lstQuestsFragment);
        questAdapter = new MyQuestAdapter(getContext(), R.layout.quest_item_layout);
        lstQuests.setAdapter(questAdapter);

        // Initialize Firebase
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
