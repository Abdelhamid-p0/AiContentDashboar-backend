package com.quiz.ai.quizModule.dto.question;

import com.quiz.ai.quizModule.entity.question.SubQuestion;

public record CorrectionSubQuestionResponse(
        String id,
        String question) {
    public static CorrectionSubQuestionResponse fromEntity(SubQuestion subQuestion) {
        return new CorrectionSubQuestionResponse(
                subQuestion.getId(),
                subQuestion.getQuestion());
    }
}
