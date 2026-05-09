package com.example.bookbuddy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookbuddy.R;
import com.example.bookbuddy.data.AppDatabase;
import com.example.bookbuddy.data.entity.Book;
import com.example.bookbuddy.ui.BookDetailActivity;
import com.example.bookbuddy.ui.adapter.BookAdapter;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class BookshelfFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView emptyView;
    private TabLayout tabLayout;
    private SearchView searchView;
    private BookAdapter adapter;
    private AppDatabase db;
    private String currentFilter = "all";
    private String currentQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);

        recyclerView = view.findViewById(R.id.bookshelf_recycler);
        emptyView = view.findViewById(R.id.empty_view);
        tabLayout = view.findViewById(R.id.tab_layout);
        searchView = view.findViewById(R.id.search_view);
        db = AppDatabase.getInstance(requireContext());

        adapter = new BookAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        adapter.setOnBookClickListener(book -> {
            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
            intent.putExtra("isbn", book.getIsbn());
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("coverUrl", book.getCoverUrl());
            intent.putExtra("publisher", book.getPublisher());
            intent.putExtra("publishDate", book.getPublishDate());
            intent.putExtra("pageCount", book.getPageCount());
            intent.putExtra("description", book.getDescription());
            startActivity(intent);
        });

        adapter.setOnBookLongClickListener(book -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("确认删除")
                    .setMessage("将从书柜中移除 \"" + book.getTitle() + "\"")
                    .setPositiveButton("删除", (d, w) -> {
                        new Thread(() -> {
                            db.bookDao().delete(book);
                            requireActivity().runOnUiThread(this::loadBooks);
                        }).start();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                loadBooks();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                loadBooks();
                return true;
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 1: currentFilter = "want_read"; break;
                    case 2: currentFilter = "reading"; break;
                    case 3: currentFilter = "finished"; break;
                    default: currentFilter = "all"; break;
                }
                loadBooks();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBooks();
    }

    private void loadBooks() {
        new Thread(() -> {
            List<Book> allBooks = db.bookDao().getAllBooksSync();
            String filter = currentFilter;
            String query = currentQuery.toLowerCase();

            List<Book> filtered = new java.util.ArrayList<>();
            for (Book b : allBooks) {
                if (!"all".equals(filter) && !filter.equals(b.getStatus())) {
                    continue;
                }
                if (!query.isEmpty() && b.getTitle() != null
                        && !b.getTitle().toLowerCase().contains(query)) {
                    continue;
                }
                filtered.add(b);
            }
            requireActivity().runOnUiThread(() -> {
                adapter.setBooks(filtered);
                if (filtered.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }
            });
        }).start();
    }
}
