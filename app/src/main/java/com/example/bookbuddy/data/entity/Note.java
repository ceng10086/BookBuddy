package com.example.bookbuddy.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long bookId;
    private String content;
    private long updateTime;

    public Note() {}

    @Ignore
    public Note(long bookId, String content) {
        this.bookId = bookId;
        this.content = content;
        this.updateTime = System.currentTimeMillis();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getBookId() { return bookId; }
    public void setBookId(long bookId) { this.bookId = bookId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
}
