package com.quiz.ai.quizModule.dto.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.quizModule.entity.quiz.Quiz;

public record QuizItemResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("type") String type) {

    public static QuizItemResponse fromEntity(Quiz quiz) {
        return new QuizItemResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getType() == null ? null : quiz.getType().name());
    }
}
