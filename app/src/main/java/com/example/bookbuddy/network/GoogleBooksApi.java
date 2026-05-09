package com.example.bookbuddy.network;

import com.example.bookbuddy.network.model.BooksResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleBooksApi {
    @GET("books/v1/volumes")
    Call<BooksResponse> searchByIsbn(@Query("q") String isbnQuery);
}
