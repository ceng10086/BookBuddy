package com.example.bookbuddy.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LlmResponse {
    @SerializedName("choices")
    private List<Choice> choices;

    public List<Choice> getChoices() { return choices; }

    public String getContent() {
        if (choices != null && !choices.isEmpty()) {
            Choice choice = choices.get(0);
            if (choice.getMessage() != null) {
                return choice.getMessage().getContent();
            }
        }
        return "AI暂未给出回复";
    }

    public static class Choice {
        @SerializedName("message")
        private Message message;

        public Message getMessage() { return message; }

        public static class Message {
            private String content;

            public String getContent() { return content; }
        }
    }
}
