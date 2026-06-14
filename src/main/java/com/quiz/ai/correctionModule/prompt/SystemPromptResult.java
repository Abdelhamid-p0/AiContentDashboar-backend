package com.quiz.ai.correctionModule.prompt;

import com.quiz.ai.correctionModule.dto.RagContextDocumentResponse;

import java.util.List;

public record SystemPromptResult(
        String message,
        List<RagContextDocumentResponse> ragContext) {
}
