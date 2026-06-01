package com.quiz.ai.application.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DomainSummaryResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title) {
}