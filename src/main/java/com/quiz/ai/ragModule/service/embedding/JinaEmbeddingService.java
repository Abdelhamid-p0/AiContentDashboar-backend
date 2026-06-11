package com.quiz.ai.ragModule.service.embedding;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.quiz.ai.ragModule.dto.embedding.JinaEmbeddingRequest;
import com.quiz.ai.ragModule.dto.embedding.JinaEmbeddingResponse;

import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JinaEmbeddingService {

    private final WebClient webClient;

    @Value("${jina.api.key}")
    private String apiKey;

    public List<Double> generateEmbedding(String text) {

        JinaEmbeddingRequest request = new JinaEmbeddingRequest(
                "jina-embeddings-v3",
                "retrieval.passage",
                true,
                List.of(text));

        JinaEmbeddingResponse response = webClient.post()
                .uri("https://api.jina.ai/v1/embeddings")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JinaEmbeddingResponse.class)
                .block();

        return response.data().get(0).embedding();
    }
}