package com.quiz.ai.infrastructure.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "quiz-ai.llm")
public class LLMProperties {
    private String baseUrl = "https://api.groq.com/openai/v1";
    private String apiKey;
    private String model = "openai/gpt-oss-120b";
    private String timeout = "30s";
    private Integer maxTokens = 2000;
    private Double temperature = 0.7;
}
