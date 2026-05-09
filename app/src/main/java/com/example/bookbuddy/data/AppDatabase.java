package com.example.bookbuddy.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.bookbuddy.data.dao.BookDao;
import com.example.bookbuddy.data.dao.NoteDao;
import com.example.bookbuddy.data.entity.Book;
import com.example.bookbuddy.data.entity.Note;

@Database(entities = {Book.class, Note.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract BookDao bookDao();
    public abstract NoteDao noteDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "bookbuddy.db"
                    ).build();
                }
            }
        }
        return instance;
    }
}
