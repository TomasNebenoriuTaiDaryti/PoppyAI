package com.example.poppyai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/movies/search").permitAll() // ⬅ allow public access here
                        .anyRequest().authenticated() // ⬅ all other routes require login
                )
                .formLogin(); // or disable login completely with `.disable()`
        return http.build();
    }
}
