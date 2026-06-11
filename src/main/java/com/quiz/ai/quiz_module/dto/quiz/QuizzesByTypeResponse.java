package com.quiz.ai.quiz_module.dto.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuizzesByTypeResponse(
                @JsonProperty("flashcards") List<QuizItemResponse> flashcards,
                @JsonProperty("quizzes") List<QuizItemResponse> quizzes) {
}
