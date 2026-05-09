package com.example.bookbuddy.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bookbuddy.R;
import com.example.bookbuddy.util.PreferencesHelper;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsFragment extends Fragment {
    private TextInputEditText apiEndpoint, apiKey, modelName;
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
        Button saveBtn = view.findViewById(R.id.btn_save_settings);

        apiEndpoint.setText(prefs.getApiEndpoint());
        apiKey.setText(prefs.getApiKey());
        modelName.setText(prefs.getModelName());

        saveBtn.setOnClickListener(v -> {
            prefs.setApiEndpoint(apiEndpoint.getText().toString().trim());
            prefs.setApiKey(apiKey.getText().toString().trim());
            prefs.setModelName(modelName.getText().toString().trim());
            Toast.makeText(getContext(), "配置已保存", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
