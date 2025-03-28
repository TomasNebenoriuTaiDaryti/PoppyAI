// src/main/java/com/example/poppyai/service/ConsoleInteractionService.java
package com.example.poppyai.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@Profile("console")  // Only activate with "console" profile
public class ConsoleInteractionService implements CommandLineRunner {

    private final ConversationService conversationService;
    private final DeepseekService deepseekService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleInteractionService(ConversationService conversationService,
                                     DeepseekService deepseekService) {
        this.conversationService = conversationService;
        this.deepseekService = deepseekService;
    }

    @Override
    public void run(String... args) {
        String sessionId = conversationService.startConversation();
        ConversationService.ConversationState state =
                conversationService.getState(sessionId);

        System.out.println("\n=== Movie Recommendation Quiz ===");

        for (int i = 0; i < 10; i++) {
            System.out.printf("\nQuestion %d/10: %s\n",
                    state.getAnswerCount() + 1,
                    state.getCurrentQuestion());

            String answer = getValidAnswer();
            state.getAnswers().add(answer);
            state.setAnswerCount(state.getAnswerCount() + 1);

            if (state.getAnswerCount() < 10) {
                String nextQuestion = deepseekService.generateNextQuestion(
                        state.getAnswers()
                );
                state.setCurrentQuestion(nextQuestion);
            }
        }

        System.out.println("\nAnalyzing your preferences...");
        List<String> recommendations = deepseekService.getRecommendations(
                state.getAnswers()
        );

        System.out.println("\nRecommended Movies:");
        recommendations.forEach(movie ->
                System.out.println("🍿 " + movie)
        );
    }

    private String getValidAnswer() {
        while (true) {
            System.out.print("Your answer (yes/no): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("yes") || input.equals("no")) {
                return input;
            }
            System.out.println("Invalid input! Please enter 'yes' or 'no'.");
        }
    }
}