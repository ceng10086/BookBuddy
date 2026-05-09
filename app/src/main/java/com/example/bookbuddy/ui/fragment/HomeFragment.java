package com.example.bookbuddy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bookbuddy.R;
import com.example.bookbuddy.data.AppDatabase;
import com.example.bookbuddy.data.entity.Book;
import com.example.bookbuddy.ui.BookDetailActivity;
import com.example.bookbuddy.ui.ScanActivity;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView statTotal, statReading, statFinished;
    private RecyclerView recentRecycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        statTotal = view.findViewById(R.id.stat_total);
        statReading = view.findViewById(R.id.stat_reading);
        statFinished = view.findViewById(R.id.stat_finished);
        recentRecycler = view.findViewById(R.id.recent_recycler);

        recentRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        view.findViewById(R.id.fab_scan).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ScanActivity.class));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
        loadRecentBooks();
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

    private void loadRecentBooks() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        new Thread(() -> {
            List<Book> all = db.bookDao().getAllBooksSync();
            List<Book> recent = all.size() > 6 ? all.subList(0, 6) : all;
            requireActivity().runOnUiThread(() -> {
                recentRecycler.setAdapter(new RecentBookAdapter(recent));
            });
        }).start();
    }

    static class RecentBookAdapter extends RecyclerView.Adapter<RecentBookAdapter.VH> {
        private final List<Book> books;

        RecentBookAdapter(List<Book> books) { this.books = books; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_book_horizontal, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Book book = books.get(position);
            holder.titleView.setText(book.getTitle());
            if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
                Glide.with(holder.coverView.getContext()).load(book.getCoverUrl()).into(holder.coverView);
            }
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), BookDetailActivity.class);
                intent.putExtra("isbn", book.getIsbn());
                intent.putExtra("title", book.getTitle());
                intent.putExtra("author", book.getAuthor());
                intent.putExtra("coverUrl", book.getCoverUrl());
                intent.putExtra("publisher", book.getPublisher());
                intent.putExtra("publishDate", book.getPublishDate());
                intent.putExtra("pageCount", book.getPageCount());
                intent.putExtra("description", book.getDescription());
                v.getContext().startActivity(intent);
            });
        }

        @Override public int getItemCount() { return books.size(); }

        static class VH extends RecyclerView.ViewHolder {
            ImageView coverView;
            TextView titleView;
            VH(View v) {
                super(v);
                coverView = v.findViewById(R.id.item_h_cover);
                titleView = v.findViewById(R.id.item_h_title);
            }
        }
    }
}
