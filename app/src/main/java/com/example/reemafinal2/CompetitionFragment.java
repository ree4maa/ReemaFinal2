package com.example.reemafinal2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//import com.google.ai.client.generativeai.Chat;
import com.google.android.material.card.MaterialCardView;



public class CompetitionFragment extends Fragment {

    private MaterialCardView cardFriends;
    private MaterialCardView cardRandom;
    private MaterialCardView cardAI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_competition, container, false);

        // Initialize Cards
        cardFriends = view.findViewById(R.id.cardFriends);
        cardRandom = view.findViewById(R.id.cardRandom);
        cardAI = view.findViewById(R.id.cardAI);

        // Play with Friends
        cardFriends.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Opening Friends List...", Toast.LENGTH_SHORT).show();
            // Intent i = new Intent(getActivity(), FriendsListActivity.class);
            // startActivity(i);
        });

        // Random Match
        cardRandom.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Searching for Opponent...", Toast.LENGTH_SHORT).show();
            /// Logic for Firebase Matchmaking goes here

        });
        return view;
    }
}