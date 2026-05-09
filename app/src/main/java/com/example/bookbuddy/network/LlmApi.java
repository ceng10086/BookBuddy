package com.example.bookbuddy.network;

import com.example.bookbuddy.network.model.LlmRequest;
import com.example.bookbuddy.network.model.LlmResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LlmApi {
    @POST("v1/chat/completions")
    Call<LlmResponse> chat(@Header("Authorization") String authHeader, @Body LlmRequest request);
}
