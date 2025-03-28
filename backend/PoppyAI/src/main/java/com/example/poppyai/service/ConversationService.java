package com.example.poppyai.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationService {

    private final Map<String, ConversationState> sessions = new ConcurrentHashMap<>();
    private final List<String> presetQuestions = List.of(
            "Do you prefer movies from this decade?",
            "Are you interested in award-winning films?",
            "Do you enjoy superhero movies?"
    );

    public String startConversation() {
        String sessionId = UUID.randomUUID().toString();
        ConversationState state = new ConversationState();
        state.setCurrentQuestion(getRandomPresetQuestion());
        sessions.put(sessionId, state);
        return sessionId;
    }

    public ConversationState getState(String sessionId) {
        return sessions.get(sessionId);
    }

    public void updateState(String sessionId, ConversationState state) {
        sessions.put(sessionId, state);
    }

    private String getRandomPresetQuestion() {
        return presetQuestions.get(new Random().nextInt(presetQuestions.size()));
    }

    public static class ConversationState {
        private int answerCount = 0;
        private List<String> answers = new ArrayList<>();
        private String currentQuestion;

        // Getters and setters
        public int getAnswerCount() { return answerCount; }
        public void setAnswerCount(int answerCount) { this.answerCount = answerCount; }
        public List<String> getAnswers() { return answers; }
        public String getCurrentQuestion() { return currentQuestion; }
        public void setCurrentQuestion(String currentQuestion) { this.currentQuestion = currentQuestion; }
    }
}