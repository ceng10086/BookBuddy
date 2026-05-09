package com.example.bookbuddy.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BooksResponse {
    @SerializedName("items")
    private List<BookItem> items;

    public List<BookItem> getItems() { return items; }
    public void setItems(List<BookItem> items) { this.items = items; }
}
