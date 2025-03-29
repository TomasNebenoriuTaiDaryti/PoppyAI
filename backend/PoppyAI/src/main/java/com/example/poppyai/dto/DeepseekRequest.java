package com.example.poppyai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeepseekRequest {
    private String model;
    private List<Message> messages;
}
