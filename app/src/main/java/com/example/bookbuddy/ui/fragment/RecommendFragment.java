package com.example.bookbuddy.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookbuddy.R;
import com.example.bookbuddy.data.AppDatabase;
import com.example.bookbuddy.data.entity.Book;
import com.example.bookbuddy.network.LlmApi;
import com.example.bookbuddy.network.RetrofitClient;
import com.example.bookbuddy.network.model.LlmRequest;
import com.example.bookbuddy.network.model.LlmResponse;
import com.example.bookbuddy.ui.BookDetailActivity;
import com.example.bookbuddy.util.PreferencesHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendFragment extends Fragment {
    private static final String TAG = "RecommendFragment";
    private EditText preferenceInput;
    private Button btnRecommend, btnShelfRecommend;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private View loadingLayout;
    private RecommendAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        preferenceInput = view.findViewById(R.id.preference_input);
        btnRecommend = view.findViewById(R.id.btn_recommend);
        btnShelfRecommend = view.findViewById(R.id.btn_shelf_recommend);
        recyclerView = view.findViewById(R.id.recommend_recycler);
        emptyView = view.findViewById(R.id.recommend_empty);
        loadingLayout = view.findViewById(R.id.loading_layout);

        adapter = new RecommendAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        btnRecommend.setOnClickListener(v -> {
            String pref = preferenceInput.getText().toString().trim();
            if (pref.isEmpty()) {
                Toast.makeText(getContext(), "请输入你的阅读偏好", Toast.LENGTH_SHORT).show();
                return;
            }
            requestRecommend(pref, false);
        });

        btnShelfRecommend.setOnClickListener(v -> {
            requestRecommend("", true);
        });

        return view;
    }

    private void requestRecommend(String userInput, boolean useShelf) {
        PreferencesHelper prefs = new PreferencesHelper(requireContext());
        String apiKey = prefs.getApiKey();
        if (apiKey.isEmpty()) {
            Toast.makeText(getContext(), "请先在「设置」中配置LLM API Key", Toast.LENGTH_LONG).show();
            return;
        }

        loadingLayout.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        new Thread(() -> {
            String userMessage;
            if (useShelf) {
                List<Book> books = AppDatabase.getInstance(requireContext()).bookDao().getAllBooksSync();
                if (books.isEmpty()) {
                    requireActivity().runOnUiThread(() -> {
                        loadingLayout.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "书柜为空，请先录入藏书", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                StringBuilder sb = new StringBuilder("我的书柜里有以下书籍：\n");
                for (Book b : books) {
                    sb.append("- ").append(b.getTitle()).append(" (").append(b.getAuthor()).append(")\n");
                }
                sb.append("\n请根据以上藏书分析我的阅读品味，推荐5本我没有的、可能感兴趣的书。请严格按以下格式输出每一行：\n《书名》 - 作者 - 推荐理由一句话");
                userMessage = sb.toString();
            } else {
                userMessage = "请推荐5本书。我的偏好是：" + userInput +
                        "\n请严格按以下格式输出每一行：\n《书名》 - 作者 - 推荐理由一句话";
            }

            String model = prefs.getModelName();
            String endpoint = prefs.getApiEndpoint();
            LlmApi api = RetrofitClient.getLlmApi(endpoint);
            List<LlmRequest.Message> messages = Arrays.asList(
                    new LlmRequest.Message("system", "你是一位专业的图书推荐助手，推荐时使用中文书名和作者。"),
                    new LlmRequest.Message("user", userMessage)
            );
            LlmRequest request = new LlmRequest(model, messages, 0.7);

            api.chat("Bearer " + apiKey, request).enqueue(new Callback<LlmResponse>() {
                @Override
                public void onResponse(Call<LlmResponse> call, Response<LlmResponse> response) {
                    Log.d(TAG, "LLM response code: " + response.code());
                    requireActivity().runOnUiThread(() -> loadingLayout.setVisibility(View.GONE));
                    if (response.isSuccessful() && response.body() != null) {
                        String content = response.body().getContent();
                        Log.d(TAG, "LLM content: " + content);
                        List<RecommendItem> items = parseRecommendations(content);
                        if (items.isEmpty()) {
                            requireActivity().runOnUiThread(() -> {
                                emptyView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                adapter.setItems(items);
                                emptyView.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            });
                        }
                    } else {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                "AI请求失败: " + response.code(), Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(Call<LlmResponse> call, Throwable t) {
                    Log.e(TAG, "LLM error", t);
                    requireActivity().runOnUiThread(() -> {
                        loadingLayout.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }).start();
    }

    private List<RecommendItem> parseRecommendations(String content) {
        List<RecommendItem> items = new ArrayList<>();
        if (content == null) return items;
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;
            // Try to parse: 《书名》 - 作者 - 理由   or   1. 《书名》 - 作者 - 理由
            String cleaned = line.replaceFirst("^\\d+[\\.\\)、]\\s*", "");
            int start = cleaned.indexOf("《");
            int end = cleaned.indexOf("》", start + 1);
            if (start >= 0 && end > start) {
                String title = cleaned.substring(start + 1, end);
                String rest = cleaned.substring(end + 1).trim();
                String author = "未知作者";
                String reason = "";
                int dashIdx = rest.indexOf(" - ");
                if (dashIdx > 0) {
                    author = rest.substring(0, dashIdx).trim();
                    reason = rest.substring(dashIdx + 3).trim();
                } else {
                    reason = rest;
                }
                // Clean up author prefix
                if (author.startsWith("- ")) author = author.substring(2);
                items.add(new RecommendItem(title, author, reason));
            }
        }
        return items;
    }

    static class RecommendItem {
        String title, author, reason;
        RecommendItem(String t, String a, String r) { title = t; author = a; reason = r; }
    }

    class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.VH> {
        private List<RecommendItem> items = new ArrayList<>();

        void setItems(List<RecommendItem> items) { this.items = items; notifyDataSetChanged(); }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recommend, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            RecommendItem item = items.get(position);
            holder.titleView.setText(item.title);
            holder.authorView.setText(item.author);
            holder.reasonView.setText(item.reason);
            holder.detailBtn.setOnClickListener(v -> {
                // Search Google Books by title
                new Thread(() -> {
                    try {
                        var api = RetrofitClient.getGoogleBooksApi();
                        var resp = api.searchByIsbn(item.title).execute();
                        if (resp.isSuccessful() && resp.body() != null
                                && resp.body().getItems() != null && !resp.body().getItems().isEmpty()) {
                            var vi = resp.body().getItems().get(0).getVolumeInfo();
                            Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                            intent.putExtra("isbn", resp.body().getItems().get(0).getId());
                            intent.putExtra("title", vi.getTitle());
                            intent.putExtra("author", vi.getFirstAuthor());
                            intent.putExtra("coverUrl", vi.getThumbnailUrl());
                            intent.putExtra("publisher", vi.getPublisher());
                            intent.putExtra("publishDate", vi.getPublishedDate());
                            intent.putExtra("pageCount", vi.getPageCount());
                            intent.putExtra("description", vi.getDescription());
                            startActivity(intent);
                        } else {
                            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                    "未找到 \"" + item.title + "\" 的详细信息", Toast.LENGTH_SHORT).show());
                        }
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                "搜索失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            });
        }

        @Override public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView titleView, authorView, reasonView;
            Button detailBtn;
            VH(View v) {
                super(v);
                titleView = v.findViewById(R.id.rec_title);
                authorView = v.findViewById(R.id.rec_author);
                reasonView = v.findViewById(R.id.rec_reason);
                detailBtn = v.findViewById(R.id.btn_view_detail);
            }
        }
    }
}
