package com.quiz.ai.quizModule.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DomainSummaryResponse(
                @JsonProperty("id") String id,
                @JsonProperty("title") String title) {
}