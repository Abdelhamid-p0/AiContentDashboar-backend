package com.quiz.ai.correctionModule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.correctionModule.entity.QuestionCorrectionFeedback;

import java.time.Instant;

public record QuestionCorrectionFeedbackResponse(
        String id,
        @JsonProperty("question_id") String questionId,
        Integer rating,
        String comment,
        @JsonProperty("created_at") Instant createdAt) {

    public static QuestionCorrectionFeedbackResponse fromEntity(QuestionCorrectionFeedback feedback) {
        return new QuestionCorrectionFeedbackResponse(
                feedback.getId(),
                feedback.getQuestionId(),
                feedback.getRating(),
                feedback.getComment(),
                feedback.getCreatedAt());
    }
}