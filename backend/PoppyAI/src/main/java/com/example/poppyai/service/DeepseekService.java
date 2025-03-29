package com.example.poppyai.service;

import com.example.poppyai.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeepseekService {

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.model}")
    private String model;

    private final RestTemplate restTemplate;

    // ✅ Constructor using Qualifier to match the bean name
    public DeepseekService(@Qualifier("deepseekRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateNextQuestion(List<String> previousQuestions, List<String> answers) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system",
                "You are a movie recommendation assistant. without any additional chatter, generate 10 concise yes/no questions. Give only one question at a time, don't repeat questions. Give questions based on finding out the mood of the user, ask general questions, but not specifically about movies/genres/themes. Questions must be sent without anything else."));



        for (int i = 0; i < answers.size(); i++) {
            messages.add(new Message("assistant", previousQuestions.get(i)));
            messages.add(new Message("user", answers.get(i)));
        }

        DeepseekRequest request = new DeepseekRequest(model, messages);
        DeepseekResponse response = restTemplate.postForObject(apiUrl, request, DeepseekResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("Invalid response from Deepseek");
        }

        return response.getChoices().get(0).getMessage().getContent();
    }

    public String generateRecommendations(List<String> answers) {
        StringBuilder userPrompt = new StringBuilder("Based on the following answers, recommend 3 movies (send the movies in 3 different lines without any formatting, just the movie name):\n");
        for (int i = 0; i < answers.size(); i++) {
            userPrompt.append("Q").append(i + 1).append(": ").append(answers.get(i)).append("\n");
        }

        DeepseekRequest request = new DeepseekRequest(model, List.of(
                new Message("system", "You are a movie expert. Based on the following answers, recommend 3 movies (send the movies in 3 different lines without any formatting, just the movie name)."),
                new Message("user", userPrompt.toString())
        ));

        DeepseekResponse response = restTemplate.postForObject(apiUrl, request, DeepseekResponse.class);

        return response != null && !response.getChoices().isEmpty()
                ? response.getChoices().get(0).getMessage().getContent()
                : "No recommendation available.";
    }
}
