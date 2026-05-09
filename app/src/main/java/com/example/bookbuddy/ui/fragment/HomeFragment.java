package com.example.bookbuddy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.bookbuddy.R;
import com.example.bookbuddy.data.AppDatabase;
import com.example.bookbuddy.ui.ScanActivity;

public class HomeFragment extends Fragment {
    private TextView statTotal, statReading, statFinished;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        statTotal = view.findViewById(R.id.stat_total);
        statReading = view.findViewById(R.id.stat_reading);
        statFinished = view.findViewById(R.id.stat_finished);

        view.findViewById(R.id.fab_scan).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ScanActivity.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    private void loadStats() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        new Thread(() -> {
            int total = db.bookDao().getAllBooksSync().size();
            int reading = db.bookDao().getBooksByStatusSync("reading").size();
            int finished = db.bookDao().getBooksByStatusSync("finished").size();
            requireActivity().runOnUiThread(() -> {
                statTotal.setText(String.valueOf(total));
                statReading.setText(String.valueOf(reading));
                statFinished.setText(String.valueOf(finished));
            });
        }).start();
    }
}
