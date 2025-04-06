package com.example.poppyai;

import com.example.poppyai.service.DeepseekService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RecommendationsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeepseekService deepseekService;

    private final String RECOMMENDATION_RESPONSE = "Movie1\nMovie2\nMovie3";

    @BeforeEach
    void setUp() {
        when(deepseekService.generateNextQuestion(anyList(), anyList()))
                .thenReturn("Are you in a good mood today?");
        when(deepseekService.generateRecommendations(anyList(), anyList()))
                .thenReturn(RECOMMENDATION_RESPONSE);
    }

    @Test
    void whenAnswerCountReachesTen_thenReturnRecommendations() throws Exception {
        MvcResult startResult = mockMvc.perform(post("/api/conversation/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andReturn();

        String jsonStart = startResult.getResponse().getContentAsString();
        String sessionId = jsonStart.split("\"sessionId\":\"")[1].split("\"")[0];

        for (int i = 0; i < 10; i++) {
            String answerPayload = "{\"sessionId\":\"" + sessionId + "\", \"answer\":\"yes\"}";
            mockMvc.perform(post("/api/conversation/answer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(answerPayload))
                    .andExpect(status().isOk());
        }

        String finalAnswerPayload = "{\"sessionId\":\"" + sessionId + "\", \"answer\":\"yes\"}";
        mockMvc.perform(post("/api/conversation/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(finalAnswerPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations").value(RECOMMENDATION_RESPONSE));
    }

    @Test
    void whenInvalidAnswerProvided_thenReturnBadRequest() throws Exception {
        MvcResult startResult = mockMvc.perform(post("/api/conversation/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").exists())
                .andReturn();

        String jsonStart = startResult.getResponse().getContentAsString();
        String sessionId = jsonStart.split("\"sessionId\":\"")[1].split("\"")[0];

        String invalidPayload = "{\"sessionId\":\"" + sessionId + "\", \"answer\":\"maybe\"}";
        mockMvc.perform(post("/api/conversation/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid answer. Allowed values: yes/no/skip")));
    }
}
