package com.example.bookbuddy.util;

import android.app.Activity;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtil {
    public static void applyTheme(Activity activity) {
        int mode = new PreferencesHelper(activity).getThemeMode();
        switch (mode) {
            case 1: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); break;
            case 2: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); break;
            default: AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM); break;
        }
    }
}
