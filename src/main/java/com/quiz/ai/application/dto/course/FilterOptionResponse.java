package com.quiz.ai.application.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FilterOptionResponse(
        @JsonProperty("value") String value,
        @JsonProperty("label") String label) {
}
