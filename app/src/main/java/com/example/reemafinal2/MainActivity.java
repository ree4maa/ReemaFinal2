package com.example.reemafinal2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.reemafinal2.data.AppDatabase;
import com.example.reemafinal2.data.MyTasksTable.MyQuest;
import com.example.reemafinal2.data.MyTasksTable.MyQuestAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Spinner spnQuests;
    private FloatingActionButton fabAddQuest;
    private ListView lstQuests;
    private MyQuestAdapter QuestAdapter;
    private Button btnAddQuest;
    private boolean isAdmin = true; // عدلي حسب التحقق الحقيقي من المستخدم

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAddQuest = findViewById(R.id.btnAddQuest);

// إظهار الزر فقط إذا كان المستخدم Admin
        if (isAdmin) {
            btnAddQuest.setVisibility(View.VISIBLE);
        } else {
            btnAddQuest.setVisibility(View.GONE);
        }

// عند الضغط على الزر → فتح AddQuest
        btnAddQuest.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddQuest.class);
            startActivity(intent);
        });
        lstQuests = findViewById(R.id.lstQuest);
        QuestAdapter = new MyQuestAdapter(this, R.layout.quest_item_layout);
        lstQuests.setAdapter(QuestAdapter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_quests) {
                    selectedFragment = new QuestsFragment();
                } else if (id == R.id.nav_level) {
                    selectedFragment = new LevelFragment();
                } else if (id == R.id.nav_rewards) {
                    selectedFragment = new RewardsFragment();
                } else if (id == R.id.nav_competition) {
                    selectedFragment = new CompetitionFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<MyQuest> allQuests = AppDatabase.getDp(this).myTaskQuery().getAllTasks();
        QuestAdapter.clear();
        QuestAdapter.addAll(allQuests);
        QuestAdapter.notifyDataSetChanged();
    }
}
