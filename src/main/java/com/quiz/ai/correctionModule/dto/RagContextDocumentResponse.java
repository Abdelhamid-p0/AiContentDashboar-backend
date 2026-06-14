package com.quiz.ai.correctionModule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.ragModule.dto.RagRetrievedDocument;

public record RagContextDocumentResponse(
        String title,
        @JsonProperty("document_type") String documentType) {
    public static RagContextDocumentResponse fromRagDocument(RagRetrievedDocument document) {
        return new RagContextDocumentResponse(
                document.title(),
                document.documentType());
    }
}
