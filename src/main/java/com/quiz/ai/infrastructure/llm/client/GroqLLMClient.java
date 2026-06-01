package com.quiz.ai.infrastructure.llm.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.ai.infrastructure.llm.config.LLMProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "quiz-ai.llm.mock-enabled", havingValue = "false", matchIfMissing = false)
@RequiredArgsConstructor
public class GroqLLMClient implements LLMClient {
    private static final String CHAT_ENDPOINT = "/chat/completions";

    private final LLMProperties llmProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public String chat(String systemMessage, String userMessage) {
        try {
            var response = callGroqAPI(systemMessage, userMessage);
            return extractContent(response);
        } catch (Exception e) {
            log.error("Error calling Groq LLM: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call LLM", e);
        }
    }

    @Override
    public <T> T chatWithStructuredResponse(String systemMessage, String userMessage, Class<T> responseType) {
        try {
            String jsonResponse = normalizeJsonResponse(chat(systemMessage, userMessage));
            return objectMapper.readValue(jsonResponse, responseType);
        } catch (Exception e) {
            log.error("Error parsing structured response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse LLM response", e);
        }
    }

    private Map<String, Object> callGroqAPI(String systemMessage, String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(llmProperties.getApiKey());

        Map<String, Object> payload = buildPayload(systemMessage, userMessage);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = llmProperties.getBaseUrl() + CHAT_ENDPOINT;
        log.debug("Calling Groq API at: {}", url);

        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
        log.debug("Groq API response: {}", response);
        return response;
    }

    private Map<String, Object> buildPayload(String systemMessage, String userMessage) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", llmProperties.getModel());
        payload.put("temperature", llmProperties.getTemperature());
        payload.put("max_tokens", llmProperties.getMaxTokens());
        payload.put("messages", List.of(
                buildMessage("system", systemMessage),
                buildMessage("user", userMessage)));
        return payload;
    }

    private Map<String, String> buildMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String normalizeJsonResponse(String response) {
        if (response == null) {
            return null;
        }

        String trimmed = response.trim();
        if (!trimmed.startsWith("```")) {
            return trimmed;
        }

        trimmed = trimmed.replaceFirst("^```(?:json)?\\s*", "");
        trimmed = trimmed.replaceFirst("\\s*```$", "");
        return trimmed.trim();
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("No choices in response");
            }

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String content = (String) message.get("content");

            if (content == null) {
                throw new RuntimeException("No content in message");
            }

            return content;
        } catch (Exception e) {
            log.error("Error extracting content from response: {}", response);
            throw new RuntimeException("Failed to extract content from LLM response", e);
        }
    }
}
