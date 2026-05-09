package com.example.bookbuddy.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String GOOGLE_BOOKS_BASE = "https://www.googleapis.com/";
    private static Retrofit googleBooksRetrofit;
    private static Gson gson;

    public static GoogleBooksApi getGoogleBooksApi() {
        if (googleBooksRetrofit == null) {
            googleBooksRetrofit = new Retrofit.Builder()
                    .baseUrl(GOOGLE_BOOKS_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return googleBooksRetrofit.create(GoogleBooksApi.class);
    }

    public static LlmApi getLlmApi(String baseUrl) {
        String url = baseUrl;
        if (!url.endsWith("/")) url += "/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(LlmApi.class);
    }
}
