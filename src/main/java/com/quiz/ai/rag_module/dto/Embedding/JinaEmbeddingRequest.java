package com.quiz.ai.rag_module.dto.Embedding;

import java.util.List;

public record JinaEmbeddingRequest(
        String model,
        String task,
        boolean normalized,
        List<String> input) {
}