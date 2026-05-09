package com.example.bookbuddy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.bookbuddy.R;
import com.example.bookbuddy.util.PreferencesHelper;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsFragment extends Fragment {
    private TextInputEditText apiEndpoint, apiKey, modelName;
    private RadioGroup themeGroup;
    private PreferencesHelper prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        prefs = new PreferencesHelper(requireContext());

        apiEndpoint = view.findViewById(R.id.api_endpoint);
        apiKey = view.findViewById(R.id.api_key);
        modelName = view.findViewById(R.id.model_name);
        themeGroup = view.findViewById(R.id.theme_group);
        Button saveBtn = view.findViewById(R.id.btn_save_settings);

        apiEndpoint.setText(prefs.getApiEndpoint());
        apiKey.setText(prefs.getApiKey());
        modelName.setText(prefs.getModelName());

        int theme = prefs.getThemeMode();
        if (theme == 1) themeGroup.check(R.id.theme_light);
        else if (theme == 2) themeGroup.check(R.id.theme_dark);
        else themeGroup.check(R.id.theme_system);

        themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int mode;
            if (checkedId == R.id.theme_light) mode = 1;
            else if (checkedId == R.id.theme_dark) mode = 2;
            else mode = 0;
            prefs.setThemeMode(mode);
            if (mode == 1) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            else if (mode == 2) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        });

        saveBtn.setOnClickListener(v -> {
            prefs.setApiEndpoint(apiEndpoint.getText().toString().trim());
            prefs.setApiKey(apiKey.getText().toString().trim());
            prefs.setModelName(modelName.getText().toString().trim());
            Toast.makeText(getContext(), "配置已保存", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
