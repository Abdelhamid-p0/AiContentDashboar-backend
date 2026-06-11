package com.quiz.ai.prompt_builder_module.config.llm.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.ai.prompt_builder_module.config.llm.config.LLMProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example implementation for OpenAI API
 * Uncomment @Component and set property: quiz-ai.llm.provider=openai to use
 * this
 * 
 * Currently disabled - use the configured live LLM client bean by default
 */
@Slf4j
// @Component
// @ConditionalOnProperty(name = "quiz-ai.llm.provider", havingValue = "openai")
@RequiredArgsConstructor
public class OpenAILLMClient implements LLMClient {
    private final LLMProperties llmProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHAT_ENDPOINT = "/chat/completions";

    @Override
    public String chat(String systemMessage, String userMessage) {
        try {
            var response = callOpenAIAPI(systemMessage, userMessage);
            return extractContent(response);
        } catch (Exception e) {
            log.error("Error calling OpenAI LLM: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to call LLM", e);
        }
    }

    @Override
    public <T> T chatWithStructuredResponse(String systemMessage, String userMessage, Class<T> responseType) {
        try {
            String jsonResponse = chat(systemMessage, userMessage);
            return objectMapper.readValue(jsonResponse, responseType);
        } catch (Exception e) {
            log.error("Error parsing structured response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse LLM response", e);
        }
    }

    private Map<String, Object> callOpenAIAPI(String systemMessage, String userMessage) {
        HttpHeaders headers = buildHeaders();
        Map<String, Object> payload = buildPayload(systemMessage, userMessage);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = llmProperties.getBaseUrl() + CHAT_ENDPOINT;
        log.debug("Calling OpenAI API at: {}", url);

        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
        log.debug("OpenAI API response: {}", response);
        return response;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + llmProperties.getApiKey());
        return headers;
    }

    private Map<String, Object> buildPayload(String systemMessage, String userMessage) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", llmProperties.getModel());
        payload.put("temperature", llmProperties.getTemperature());
        payload.put("max_tokens", llmProperties.getMaxTokens());

        List<Map<String, String>> messages = List.of(
                buildMessage("system", systemMessage),
                buildMessage("user", userMessage));
        payload.put("messages", messages);

        return payload;
    }

    private Map<String, String> buildMessage(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
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
