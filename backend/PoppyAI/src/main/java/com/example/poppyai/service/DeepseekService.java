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

    public DeepseekService(@Qualifier("deepseekRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateNextQuestion(List<String> previousQuestions, List<String> answers) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system",
                "Generate concise yes/no questions to determine movie preferences. "
                        + "Focus on mood like:'are u mad now?, are u happy ?', setting, themes, and watching habits. "
                        + "Example: 'Do you prefer recent releases?' "
                        + "Only provide one question with no extra text."
                        + "Do NOT give questions that are makes you choose ( this or that )"
                        + "Focus more on users mood right now at this time."
                        + "If the answer given by the user is 'skip' skip the given question and create a new question."));


        for (int i = 0; i < answers.size(); i++) {
            String answer = answers.get(i);
            if ("skip".equals(answer)) {
                messages.add(new Message("user", answer));
                continue;
            }

            messages.add(new Message("assistant", previousQuestions.get(i)));
            messages.add(new Message("user", answer));
        }

        DeepseekRequest request = new DeepseekRequest(model, messages);
        DeepseekResponse response = restTemplate.postForObject(apiUrl, request, DeepseekResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new RuntimeException("Invalid response from Deepseek");
        }

        return response.getChoices().get(0).getMessage().getContent();
    }

    public String generateRecommendations(List<String> questions, List<String> answers) {
        StringBuilder userPrompt = new StringBuilder("Based on the following conversation, recommend 3 movies (only titles, each on a new line):\n");
        for (int i = 0; i < answers.size(); i++) {
            String answer = answers.get(i);
            if ("skip".equals(answer)) continue;

            userPrompt.append("Question: ").append(questions.get(i)).append("\n");
            userPrompt.append("Answer: ").append(answer).append("\n\n");
        }

        DeepseekRequest request = new DeepseekRequest(model, List.of(
                new Message("system", "You are a movie expert. Analyze the user's preferences from the Q&A and recommend 3 relevant movies. Format: One movie per line, no extra text."),
                new Message("user", userPrompt.toString())
        ));

        DeepseekResponse response = restTemplate.postForObject(apiUrl, request, DeepseekResponse.class);

        return response != null && !response.getChoices().isEmpty()
                ? response.getChoices().get(0).getMessage().getContent()
                : "No recommendation available.";
    }
}