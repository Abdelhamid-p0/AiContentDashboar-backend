package com.quiz.ai.ragModule.dto;

import java.util.List;
import java.util.stream.Collectors;

public record PedagogicalRagContext(
        List<RagRetrievedDocument> documents) {
    public String toPromptContext() {
        if (documents == null || documents.isEmpty()) {
            return "";
        }

        return documents.stream()
                .map(RagRetrievedDocument::content)
                .collect(Collectors.joining("\n---\n"));
    }
}
