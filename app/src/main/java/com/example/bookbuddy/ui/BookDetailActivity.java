package com.example.bookbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.bookbuddy.R;
import com.example.bookbuddy.data.AppDatabase;
import com.example.bookbuddy.data.entity.Book;
import com.example.bookbuddy.network.LlmApi;
import com.example.bookbuddy.network.RetrofitClient;
import com.example.bookbuddy.network.model.LlmRequest;
import com.example.bookbuddy.network.model.LlmResponse;
import com.example.bookbuddy.util.PreferencesHelper;
import com.example.bookbuddy.util.ThemeUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.Arrays;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {
    private ImageView coverView;
    private TextView titleView, authorView, publisherView, publishDateView;
    private TextView pagesView, descriptionView;
    private ChipGroup statusGroup;
    private Chip chipWant, chipReading, chipFinished;
    private Button btnAddToShelf, btnSave, btnDelete, btnAiChat, btnWriteNote;
    private AppDatabase db;
    private Book existingBook;
    private String isbn, title, author, coverUrl, publisher;
    private String publishDate, description;
    private int pageCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        db = AppDatabase.getInstance(this);

        coverView = findViewById(R.id.book_cover);
        titleView = findViewById(R.id.book_title);
        authorView = findViewById(R.id.book_author);
        publisherView = findViewById(R.id.book_publisher);
        publishDateView = findViewById(R.id.book_publish_date);
        pagesView = findViewById(R.id.book_pages);
        descriptionView = findViewById(R.id.book_description);
        statusGroup = findViewById(R.id.status_group);
        chipWant = findViewById(R.id.chip_want);
        chipReading = findViewById(R.id.chip_reading);
        chipFinished = findViewById(R.id.chip_finished);
        btnAddToShelf = findViewById(R.id.btn_add_to_shelf);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        btnAiChat = findViewById(R.id.btn_ai_chat);
        btnWriteNote = findViewById(R.id.btn_write_note);

        readIntent();
        displayBook();
        checkExisting();
        setupListeners();
    }

    private void readIntent() {
        isbn = getIntent().getStringExtra("isbn");
        title = getIntent().getStringExtra("title");
        author = getIntent().getStringExtra("author");
        coverUrl = getIntent().getStringExtra("coverUrl");
        publisher = getIntent().getStringExtra("publisher");
        publishDate = getIntent().getStringExtra("publishDate");
        pageCount = getIntent().getIntExtra("pageCount", 0);
        description = getIntent().getStringExtra("description");
    }

    private void displayBook() {
        titleView.setText(title != null ? title : "未知书名");
        authorView.setText(author != null ? author : "未知作者");
        publisherView.setText("出版社: " + (publisher != null ? publisher : "未知"));
        publishDateView.setText("出版日期: " + (publishDate != null ? publishDate : "未知"));
        pagesView.setText(pageCount > 0 ? "页数: " + pageCount : "");
        descriptionView.setText(description != null ? description : "暂无简介");

        if (coverUrl != null && !coverUrl.isEmpty()) {
            Glide.with(this).load(coverUrl).into(coverView);
        }
    }

    private void checkExisting() {
        new Thread(() -> {
            Book book = db.bookDao().getBookByIsbn(isbn);
            existingBook = book;
            runOnUiThread(() -> {
                if (existingBook != null) {
                    btnAddToShelf.setVisibility(Button.GONE);
                    btnSave.setVisibility(Button.VISIBLE);
                    btnDelete.setVisibility(Button.VISIBLE);

                    switch (existingBook.getStatus()) {
                        case "reading": statusGroup.check(R.id.chip_reading); break;
                        case "finished": statusGroup.check(R.id.chip_finished); break;
                        default: statusGroup.check(R.id.chip_want); break;
                    }
                } else {
                    btnAddToShelf.setVisibility(Button.VISIBLE);
                    btnSave.setVisibility(Button.GONE);
                    btnDelete.setVisibility(Button.GONE);
                }
            });
        }).start();
    }

    private void setupListeners() {
        btnAddToShelf.setOnClickListener(v -> {
            new Thread(() -> {
                String status = getSelectedStatus();
                Book book = new Book(isbn, title, author, coverUrl,
                        publisher, publishDate, pageCount, description);
                book.setStatus(status);
                long id = db.bookDao().insert(book);
                existingBook = book;
                existingBook.setId(id);
                runOnUiThread(() -> {
                    Toast.makeText(this, "已加入书柜", Toast.LENGTH_SHORT).show();
                    btnAddToShelf.setVisibility(Button.GONE);
                    btnSave.setVisibility(Button.VISIBLE);
                    btnDelete.setVisibility(Button.VISIBLE);
                });
            }).start();
        });

        btnSave.setOnClickListener(v -> {
            if (existingBook != null) {
                existingBook.setStatus(getSelectedStatus());
                new Thread(() -> {
                    db.bookDao().update(existingBook);
                    runOnUiThread(() -> Toast.makeText(this,
                            "已保存", Toast.LENGTH_SHORT).show());
                }).start();
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (existingBook != null) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("确认删除")
                        .setMessage("将从书柜中移除 \"" + existingBook.getTitle() + "\"")
                        .setPositiveButton("删除", (d, w) -> {
                            new Thread(() -> {
                                db.bookDao().delete(existingBook);
                                existingBook = null;
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "已删除", Toast.LENGTH_SHORT).show();
                                    btnAddToShelf.setVisibility(Button.VISIBLE);
                                    btnSave.setVisibility(Button.GONE);
                                    btnDelete.setVisibility(Button.GONE);
                                    statusGroup.check(R.id.chip_want);
                                });
                            }).start();
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        btnWriteNote.setOnClickListener(v -> {
            if (existingBook == null) {
                Toast.makeText(this, "请先将书加入书柜", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, NoteActivity.class);
            intent.putExtra("bookId", existingBook.getId());
            intent.putExtra("bookTitle", existingBook.getTitle());
            startActivity(intent);
        });

        btnAiChat.setOnClickListener(v -> aiChatAboutBook());
    }

    private void aiChatAboutBook() {
        PreferencesHelper prefs = new PreferencesHelper(this);
        String apiKey = prefs.getApiKey();
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "请先在「设置」中配置LLM API Key", Toast.LENGTH_LONG).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_ai_chat, null);
        TextView responseText = dialogView.findViewById(R.id.ai_response);
        ProgressBar progressBar = dialogView.findViewById(R.id.ai_progress);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("AI聊聊: " + (title != null ? title : "这本书"))
                .setView(dialogView)
                .setPositiveButton("关闭", null)
                .create();
        dialog.show();

        String prompt = String.format("请用200字左右聊聊《%s》（作者：%s）这本书。可以包括：主题概述、适合什么人读、阅读建议。",
                title != null ? title : "未知",
                author != null ? author : "未知");

        new Thread(() -> {
            LlmApi api = RetrofitClient.getLlmApi(prefs.getApiEndpoint());
            LlmRequest request = new LlmRequest(prefs.getModelName(),
                    Arrays.asList(
                            new LlmRequest.Message("system", "你是一位博学的文学评论家。"),
                            new LlmRequest.Message("user", prompt)
                    ), 0.7);

            api.chat("Bearer " + apiKey, request).enqueue(new Callback<LlmResponse>() {
                @Override
                public void onResponse(Call<LlmResponse> call, Response<LlmResponse> response) {
                    runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                    if (response.isSuccessful() && response.body() != null) {
                        runOnUiThread(() -> responseText.setText(response.body().getContent()));
                    } else {
                        runOnUiThread(() -> responseText.setText("AI请求失败: " + response.code()));
                    }
                }

                @Override
                public void onFailure(Call<LlmResponse> call, Throwable t) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        responseText.setText("网络错误: " + t.getMessage());
                    });
                }
            });
        }).start();
    }

    private String getSelectedStatus() {
        int id = statusGroup.getCheckedChipId();
        if (id == R.id.chip_reading) return "reading";
        if (id == R.id.chip_finished) return "finished";
        return "want_read";
    }
}
