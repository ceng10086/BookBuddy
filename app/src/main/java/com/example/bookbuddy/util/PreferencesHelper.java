package com.example.bookbuddy.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private static final String PREFS_NAME = "bookbuddy_prefs";
    private static final String KEY_API_ENDPOINT = "api_endpoint";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_MODEL_NAME = "model_name";
    private static final String KEY_THEME_MODE = "theme_mode";

    private final SharedPreferences prefs;

    public PreferencesHelper(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getApiEndpoint() {
        return prefs.getString(KEY_API_ENDPOINT, "https://api.openai.com/v1/chat/completions");
    }

    public void setApiEndpoint(String endpoint) {
        prefs.edit().putString(KEY_API_ENDPOINT, endpoint).apply();
    }

    public String getApiKey() {
        return prefs.getString(KEY_API_KEY, "");
    }

    public void setApiKey(String key) {
        prefs.edit().putString(KEY_API_KEY, key).apply();
    }

    public String getModelName() {
        return prefs.getString(KEY_MODEL_NAME, "gpt-4o");
    }

    public void setModelName(String name) {
        prefs.edit().putString(KEY_MODEL_NAME, name).apply();
    }

    public int getThemeMode() {
        return prefs.getInt(KEY_THEME_MODE, 0); // 0=system, 1=light, 2=dark
    }

    public void setThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
    }
}
