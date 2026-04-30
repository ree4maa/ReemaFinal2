package com.example.reemafinal2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load QuestsFragment by default on start
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new QuestsFragment())
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
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
            } else if (id == R.id.nav_settings) {
                selectedFragment = new Settings_Fragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }
}
