package com.quiz.ai.ragModule.dto.embedding;

import java.util.List;

public record JinaEmbeddingResponse(
        List<EmbeddingData> data) {
}
