package com.quiz.ai.ragModule.dto.embedding;

import java.util.List;

public record JinaEmbeddingRequest(
        String model,
        String task,
        boolean normalized,
        List<String> input) {
}