// src/main/java/com/example/poppyai/config/DeepseekConfig.java
package com.example.poppyai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DeepseekConfig {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Bean
    public RestTemplate deepseekRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + apiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}