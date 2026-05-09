package com.example.bookbuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bookbuddy.R;
import com.example.bookbuddy.network.GoogleBooksApi;
import com.example.bookbuddy.network.RetrofitClient;
import com.example.bookbuddy.network.model.BooksResponse;
import com.example.bookbuddy.util.ThemeUtil;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {
    private EditText isbnInput;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    String isbn = result.getContents();
                    isbnInput.setText(isbn);
                    searchBookByIsbn(isbn);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        isbnInput = findViewById(R.id.isbn_input);
        Button scanBtn = findViewById(R.id.scan_btn);
        Button searchBtn = findViewById(R.id.search_btn);

        scanBtn.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.EAN_13);
            options.setPrompt("请扫描图书ISBN条形码");
            options.setBeepEnabled(true);
            options.setOrientationLocked(true);
            barcodeLauncher.launch(options);
        });

        searchBtn.setOnClickListener(v -> {
            String isbn = isbnInput.getText().toString().trim();
            if (!isbn.isEmpty()) {
                searchBookByIsbn(isbn);
            } else {
                Toast.makeText(this, "请输入ISBN", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchBookByIsbn(String isbn) {
        Toast.makeText(this, "正在搜索 ISBN: " + isbn, Toast.LENGTH_SHORT).show();

        GoogleBooksApi api = RetrofitClient.getGoogleBooksApi();
        api.searchByIsbn("isbn:" + isbn).enqueue(new Callback<BooksResponse>() {
            @Override
            public void onResponse(Call<BooksResponse> call, Response<BooksResponse> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getItems() != null
                        && !response.body().getItems().isEmpty()) {
                    var vi = response.body().getItems().get(0).getVolumeInfo();
                    navigateToDetail(isbn, vi.getTitle(), vi.getFirstAuthor(),
                            vi.getThumbnailUrl(), vi.getPublisher(),
                            vi.getPublishedDate(), vi.getPageCount(), vi.getDescription());
                } else {
                    Toast.makeText(ScanActivity.this, "未找到该书信息", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BooksResponse> call, Throwable t) {
                Toast.makeText(ScanActivity.this, "网络错误: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToDetail(String isbn, String title, String author,
                                   String coverUrl, String publisher, String publishDate,
                                   int pageCount, String description) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("isbn", isbn);
        intent.putExtra("title", title);
        intent.putExtra("author", author);
        intent.putExtra("coverUrl", coverUrl);
        intent.putExtra("publisher", publisher);
        intent.putExtra("publishDate", publishDate);
        intent.putExtra("pageCount", pageCount);
        intent.putExtra("description", description);
        startActivity(intent);
    }
}
