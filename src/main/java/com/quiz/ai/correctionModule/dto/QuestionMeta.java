package com.quiz.ai.correctionModule.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionMeta(
                String title,
                String summary,
                @JsonProperty("difficulty_reasoning") String difficultyReasoning) {
}