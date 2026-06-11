package com.quiz.ai.ragModule.dto.embedding;

import java.util.List;

public record EmbeddingData(
        int index,
        List<Double> embedding) {
}