package com.example.bookbuddy.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.bookbuddy.R;
import com.example.bookbuddy.databinding.ActivityMainBinding;
import com.example.bookbuddy.ui.fragment.BookshelfFragment;
import com.example.bookbuddy.ui.fragment.HomeFragment;
import com.example.bookbuddy.ui.fragment.RecommendFragment;
import com.example.bookbuddy.ui.fragment.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_bookshelf) {
                fragment = new BookshelfFragment();
            } else if (id == R.id.nav_recommend) {
                fragment = new RecommendFragment();
            } else if (id == R.id.nav_settings) {
                fragment = new SettingsFragment();
            } else {
                return false;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        });
    }
}
