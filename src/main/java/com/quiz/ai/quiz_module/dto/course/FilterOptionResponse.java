package com.quiz.ai.quiz_module.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FilterOptionResponse(
                @JsonProperty("value") String value,
                @JsonProperty("label") String label) {
}
