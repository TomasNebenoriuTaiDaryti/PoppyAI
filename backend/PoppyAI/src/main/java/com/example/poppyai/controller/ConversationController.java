package com.example.poppyai.controller;

import com.example.poppyai.service.ConversationService;
import com.example.poppyai.service.DeepseekService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/conversation")
public class ConversationController {

    private final ConversationService conversationService;
    private final DeepseekService deepseekService;

    public ConversationController(ConversationService conversationService,
                                  DeepseekService deepseekService) {
        this.conversationService = conversationService;
        this.deepseekService = deepseekService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startConversation() {
        String sessionId = conversationService.startConversation();
        ConversationService.ConversationState state = conversationService.getState(sessionId);
        return ResponseEntity.ok(createResponse(sessionId, state));
    }

    @PostMapping("/answer")
    public ResponseEntity<?> handleAnswer(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String answer = request.get("answer");

        ConversationService.ConversationState state = conversationService.getState(sessionId);
        if (state == null) {
            return ResponseEntity.badRequest().body("Invalid session ID");
        }

        state.getAnswers().add(answer);
        state.setAnswerCount(state.getAnswerCount() + 1);

        if (state.getAnswerCount() >= 10) {
            return handleRecommendationPhase(sessionId, state);
        } else {
            return handleNextQuestion(sessionId, state);
        }
    }

    private ResponseEntity<Map<String, Object>> handleRecommendationPhase(
            String sessionId,
            ConversationService.ConversationState state
    ) {
        var recommendations = deepseekService.getRecommendations(state.getAnswers());
        conversationService.updateState(sessionId, state);
        return ResponseEntity.ok(Map.of("recommendations", recommendations));
    }

    private ResponseEntity<Map<String, Object>> handleNextQuestion(
            String sessionId,
            ConversationService.ConversationState state
    ) {
        var nextQuestion = deepseekService.generateNextQuestion(state.getAnswers());
        state.setCurrentQuestion(nextQuestion);
        conversationService.updateState(sessionId, state);
        return ResponseEntity.ok(createResponse(sessionId, state));
    }

    private Map<String, Object> createResponse(String sessionId, ConversationService.ConversationState state) {
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("question", state.getCurrentQuestion());
        response.put("questionNumber", state.getAnswerCount() + 1);
        return response;
    }
}