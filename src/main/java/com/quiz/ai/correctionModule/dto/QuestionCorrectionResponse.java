package com.quiz.ai.correctionModule.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.quiz.ai.quizModule.dto.question.CorrectionQuestionResponse;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionCorrectionResponse(
        @JsonDeserialize(using = FlexibleStringListDeserializer.class) List<String> corrections,
        String explanation,
        @JsonProperty("detected_errors") String detectedErrors,
        @JsonProperty("original_question") CorrectionQuestionResponse originalQuestion,
        @JsonProperty("rag_context") List<RagContextDocumentResponse> context,
        @JsonProperty("improved_question") CorrectionQuestionResponse improvedQuestion) {
}
