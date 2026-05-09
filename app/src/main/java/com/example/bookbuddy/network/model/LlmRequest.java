package com.example.bookbuddy.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class LlmRequest {
    private String model;
    private List<Message> messages;
    private double temperature;

    public LlmRequest(String model, List<Message> messages, double temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() { return role; }
        public String getContent() { return content; }
    }
}
