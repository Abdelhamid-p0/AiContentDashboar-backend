package com.quiz.ai.correctionModule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record QuestionCorrectionFeedbackRequest(
        @NotNull @Min(1) @Max(5) Integer rating,
        String comment,
        @JsonProperty("correction") QuestionCorrectionResponse correction) {
}