package com.example.reemafinal2;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

           BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

       //  عرض أول Fragment عند فتح التطبيق
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new QuestsFragment())
                .commit();

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
    }

