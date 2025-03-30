package com.example.poppyai.controller;

import com.example.poppyai.service.ConversationService;
import com.example.poppyai.service.DeepseekService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/conversation")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
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
        String sessionId = conversationService.startConversation(deepseekService);
        ConversationService.ConversationState state = conversationService.getState(sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("question", state.getCurrentQuestion());
        response.put("questionNumber", state.getAnswerCount() + 1);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/answer")
    public ResponseEntity<?> handleAnswer(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String answer = request.get("answer").toLowerCase();

        if (!Arrays.asList("yes", "no", "skip").contains(answer)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid answer. Allowed values: yes/no/skip"));
        }

        if (sessionId == null || sessionId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing session ID"));
        }

        ConversationService.ConversationState state = conversationService.getState(sessionId);
        if (state == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid session ID"));
        }

        try {
            state.getAnswers().add(answer);

            if (!"skip".equals(answer)) {
                state.setAnswerCount(state.getAnswerCount() + 1);
            }

            if (state.getAnswerCount() >= 10) {
                return handleRecommendationPhase(sessionId, state);
            }

            String nextQuestion = conversationService.getNextQuestion(state, deepseekService);
            state.getQuestions().add(nextQuestion);
            state.setCurrentQuestion(nextQuestion);
            conversationService.updateState(sessionId, state);

            Map<String, Object> response = new HashMap<>();
            response.put("question", state.getCurrentQuestion());
            response.put("questionNumber", state.getAnswerCount() + 1);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private ResponseEntity<Map<String, Object>> handleRecommendationPhase(
            String sessionId,
            ConversationService.ConversationState state
    ) {
        try {
            var recommendations = deepseekService.generateRecommendations(
                    state.getQuestions(),
                    state.getAnswers()
            );
            Map<String, Object> response = new HashMap<>();
            response.put("recommendations", recommendations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to generate recommendations"));
        }
    }
}