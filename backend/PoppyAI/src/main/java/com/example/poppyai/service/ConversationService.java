package com.example.poppyai.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConversationService {

    private final Map<String, ConversationState> sessions = new ConcurrentHashMap<>();

    public String startConversation(DeepseekService deepseekService) {
        String sessionId = UUID.randomUUID().toString();
        ConversationState state = new ConversationState();

        String firstQuestion = deepseekService.generateNextQuestion(new ArrayList<>(), new ArrayList<>());
        state.getQuestions().add(firstQuestion);
        state.setCurrentQuestion(firstQuestion);

        sessions.put(sessionId, state);
        return sessionId;
    }

    public ConversationState getState(String sessionId) {
        return sessions.get(sessionId);
    }

    public void updateState(String sessionId, ConversationState state) {
        sessions.put(sessionId, state);
    }

    public String getNextQuestion(ConversationState state, DeepseekService deepseekService) {
        return deepseekService.generateNextQuestion(state.getQuestions(), state.getAnswers());
    }

    public static class ConversationState {
        private int answerCount = 0;
        private List<String> questions = new ArrayList<>();
        private List<String> answers = new ArrayList<>();
        private String currentQuestion;

        public int getAnswerCount() { return answerCount; }
        public void setAnswerCount(int answerCount) { this.answerCount = answerCount; }

        public List<String> getAnswers() { return answers; }
        public List<String> getQuestions() { return questions; }

        public String getCurrentQuestion() { return currentQuestion; }
        public void setCurrentQuestion(String currentQuestion) { this.currentQuestion = currentQuestion; }
    }
}
