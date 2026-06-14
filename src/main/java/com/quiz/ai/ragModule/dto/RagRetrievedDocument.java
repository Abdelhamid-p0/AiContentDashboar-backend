package com.quiz.ai.ragModule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.ragModule.entity.PedagogyDocument;

public record RagRetrievedDocument(
        String title,
        String content,
        @JsonProperty("document_type") String documentType) {
    public static RagRetrievedDocument fromEntity(PedagogyDocument document) {
        return new RagRetrievedDocument(
                document.getTitle(),
                document.getContent(),
                document.getDocumentType());
    }
}
