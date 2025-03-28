package com.example.poppyai.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeepseekResponse {
    private List<Choice> choices;
}