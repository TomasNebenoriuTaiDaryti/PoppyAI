package com.example.poppyai.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeepseekRequest {
    private String model;
    private List<Message> messages;

    public DeepseekRequest(String model, String content) {
        this.model = model;
        this.messages = List.of(
                new Message("system", "You are a movie recommendation assistant. Generate concise yes/no questions."),
                new Message("user", content)
        );
    }
}