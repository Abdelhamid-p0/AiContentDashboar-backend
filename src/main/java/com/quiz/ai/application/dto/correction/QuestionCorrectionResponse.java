package com.quiz.ai.application.dto.correction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.quiz.ai.application.dto.question.QuestionResponse;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionCorrectionResponse(
        @JsonDeserialize(using = FlexibleStringListDeserializer.class) List<String> corrections,
        String explanation,
        @JsonProperty("detected_errors") String detectedErrors,
        @JsonProperty("original_question") QuestionResponse originalQuestion,
        @JsonProperty("improved_question") QuestionResponse improvedQuestion) {
}
