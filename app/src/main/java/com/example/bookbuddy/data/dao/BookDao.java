package com.example.bookbuddy.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.bookbuddy.data.entity.Book;

import java.util.List;

@Dao
public interface BookDao {
    @Insert
    long insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("SELECT * FROM books ORDER BY addedTime DESC")
    LiveData<List<Book>> getAllBooks();

    @Query("SELECT * FROM books WHERE status = :status ORDER BY addedTime DESC")
    LiveData<List<Book>> getBooksByStatus(String status);

    @Query("SELECT * FROM books WHERE isbn = :isbn LIMIT 1")
    Book getBookByIsbn(String isbn);

    @Query("SELECT * FROM books WHERE id = :id")
    Book getBookById(long id);

    @Query("SELECT * FROM books WHERE title LIKE '%' || :query || '%' ORDER BY addedTime DESC")
    LiveData<List<Book>> searchBooks(String query);

    @Query("SELECT COUNT(*) FROM books")
    LiveData<Integer> getBookCount();

    @Query("SELECT COUNT(*) FROM books WHERE status = :status")
    LiveData<Integer> getBookCountByStatus(String status);
}
