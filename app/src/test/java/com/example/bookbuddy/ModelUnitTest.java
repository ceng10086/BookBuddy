package com.example.bookbuddy;

import com.example.bookbuddy.data.entity.Book;
import com.example.bookbuddy.data.entity.Note;
import com.example.bookbuddy.network.model.LlmResponse;

import org.junit.Test;
import static org.junit.Assert.*;

public class ModelUnitTest {

    @Test
    public void book_defaultConstructor_works() {
        Book book = new Book();
        book.setIsbn("9787115544896");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setCoverUrl("https://example.com/cover.jpg");
        book.setPublisher("Test Pub");
        book.setPublishDate("2024");
        book.setPageCount(300);
        book.setDescription("Test desc");
        book.setStatus("want_read");
        book.setAddedTime(1000L);

        assertEquals("9787115544896", book.getIsbn());
        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals("https://example.com/cover.jpg", book.getCoverUrl());
        assertEquals("Test Pub", book.getPublisher());
        assertEquals("2024", book.getPublishDate());
        assertEquals(300, book.getPageCount());
        assertEquals("Test desc", book.getDescription());
        assertEquals("want_read", book.getStatus());
        assertEquals(1000L, book.getAddedTime());
    }

    @Test
    public void book_paramConstructor_setsDefaults() {
        Book book = new Book("9787115544896", "Effective Java",
                "Joshua Bloch", "https://cover.jpg",
                "Addison-Wesley", "2017", 416, "Great book");
        assertEquals("want_read", book.getStatus());
        assertTrue(book.getAddedTime() > 0);
        assertEquals("Effective Java", book.getTitle());
    }

    @Test
    public void note_defaultConstructor_works() {
        Note note = new Note();
        note.setBookId(1L);
        note.setContent("This is a note");
        note.setUpdateTime(2000L);

        assertEquals(1L, note.getBookId());
        assertEquals("This is a note", note.getContent());
        assertEquals(2000L, note.getUpdateTime());
    }

    @Test
    public void note_paramConstructor_setsDefaults() {
        Note note = new Note(5L, "Great book!");
        assertEquals(5L, note.getBookId());
        assertEquals("Great book!", note.getContent());
        assertTrue(note.getUpdateTime() > 0);
    }

    @Test
    public void llmResponse_getContent_works() {
        // Test with valid response structure
        String json = "{\"choices\":[{\"message\":{\"content\":\"推荐《三体》\"}}]}";
        LlmResponse resp = new com.google.gson.Gson().fromJson(json, LlmResponse.class);
        assertNotNull(resp);
        assertEquals("推荐《三体》", resp.getContent());
    }

    @Test
    public void llmResponse_nullChoices_returnsDefault() {
        String json = "{}";
        LlmResponse resp = new com.google.gson.Gson().fromJson(json, LlmResponse.class);
        assertEquals("AI暂未给出回复", resp.getContent());
    }

    @Test
    public void llmResponse_emptyChoices_returnsDefault() {
        String json = "{\"choices\":[]}";
        LlmResponse resp = new com.google.gson.Gson().fromJson(json, LlmResponse.class);
        assertEquals("AI暂未给出回复", resp.getContent());
    }
}
