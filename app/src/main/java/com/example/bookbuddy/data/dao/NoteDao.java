package com.example.bookbuddy.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bookbuddy.data.entity.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    long insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY updateTime DESC")
    LiveData<List<Note>> getNotesForBook(long bookId);

    @Query("SELECT * FROM notes WHERE bookId = :bookId ORDER BY updateTime DESC")
    List<Note> getNotesForBookSync(long bookId);
}
