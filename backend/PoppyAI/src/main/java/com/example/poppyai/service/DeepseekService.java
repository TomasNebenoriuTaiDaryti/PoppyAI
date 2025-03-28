package com.example.poppyai.service;

import com.example.poppyai.dto.DeepseekRequest;
import com.example.poppyai.dto.DeepseekResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeepseekService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String model;

    public DeepseekService(RestTemplate deepseekRestTemplate,
                           @Value("${deepseek.api.url}") String apiUrl,
                           @Value("${deepseek.model}") String model) {
        this.restTemplate = deepseekRestTemplate;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    public String generateNextQuestion(List<String> answers) {
        String prompt = """
        Generate a yes/no question about movie preferences based on these previous answers: %s
        Rules:
        1. Only ask yes/no questions
        2. Format: Start with "Do you..."
        3. Be specific to movies
        4. Never ask about number of movies
        5. Example valid questions:
           - "Do you prefer horror movies?"
           - "Are you interested in documentaries?"
           - "Do you like films with female leads?"
        """.formatted(String.join(", ", answers));

        return getCompletion(prompt);
    }

    public List<String> getRecommendations(List<String> answers) {
        String prompt = """
        Generate 3 movie recommendations based on these answers: %s
        Rules:
        1. Return ONLY movie titles
        2. Comma-separated format
        3. No numbering or quotes
        4. Include diverse genres
        5. Examples:
           - The Godfather, Inception, Parasite
           - Pulp Fiction, Spirited Away, Get Out
        """.formatted(String.join(", ", answers));

        String response = getCompletion(prompt);
        return parseRecommendations(response);
    }

    private String getCompletion(String prompt) {
        try {
            DeepseekRequest request = new DeepseekRequest(model, prompt);
            DeepseekResponse response = restTemplate.postForObject(apiUrl, request, DeepseekResponse.class);

            if(response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("Invalid response from Deepseek API");
            }

            return response.getChoices().get(0).getMessage().getContent();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Deepseek API Error: " + e.getResponseBodyAsString());
        }
    }

    private List<String> parseRecommendations(String response) {
        return Arrays.stream(response.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .limit(3)
                .collect(Collectors.toList());
    }
}