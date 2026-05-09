package com.example.bookbuddy.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bookbuddy.R;
import com.example.bookbuddy.data.AppDatabase;
import com.example.bookbuddy.data.entity.Note;
import com.example.bookbuddy.util.ThemeUtil;

public class NoteActivity extends AppCompatActivity {
    private EditText contentEdit;
    private AppDatabase db;
    private long bookId;
    private Note existingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        db = AppDatabase.getInstance(this);

        TextView bookTitleView = findViewById(R.id.note_book_title);
        contentEdit = findViewById(R.id.note_content);
        Button saveBtn = findViewById(R.id.btn_save_note);

        bookId = getIntent().getLongExtra("bookId", 0);
        String bookTitle = getIntent().getStringExtra("bookTitle");
        bookTitleView.setText("《" + bookTitle + "》· 笔记");

        loadExistingNote();
        saveBtn.setOnClickListener(v -> saveNote());
    }

    private void loadExistingNote() {
        new Thread(() -> {
            var notes = db.noteDao().getNotesForBookSync(bookId);
            if (!notes.isEmpty()) {
                existingNote = notes.get(0);
                runOnUiThread(() -> contentEdit.setText(existingNote.getContent()));
            }
        }).start();
    }

    private void saveNote() {
        String content = contentEdit.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "笔记内容为空", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            if (existingNote != null) {
                existingNote.setContent(content);
                existingNote.setUpdateTime(System.currentTimeMillis());
                db.noteDao().update(existingNote);
            } else {
                existingNote = new Note(bookId, content);
                long id = db.noteDao().insert(existingNote);
                existingNote.setId(id);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "笔记已保存", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
