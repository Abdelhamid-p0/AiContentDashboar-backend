package com.quiz.ai.rag_module.dto.Embedding;

import java.util.List;

public record JinaEmbeddingResponse(
        List<EmbeddingData> data) {
}
