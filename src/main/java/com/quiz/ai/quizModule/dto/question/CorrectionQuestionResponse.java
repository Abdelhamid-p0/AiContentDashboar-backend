package com.quiz.ai.quizModule.dto.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.quizModule.entity.question.Question;

import java.util.List;

public record CorrectionQuestionResponse(
        String id,
        String question,
        @JsonProperty("question_type") String questionType,
        String feedback,
        ObjectiveResponse objective,
        @JsonProperty("sub_questions") List<CorrectionSubQuestionResponse> subQuestions) {
    public static CorrectionQuestionResponse fromEntity(Question question) {
        return new CorrectionQuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getQuestionType() == null ? null : question.getQuestionType().name(),
                question.getFeedback(),
                question.getObjective() == null ? null : ObjectiveResponse.fromEntity(question.getObjective()),
                question.getSubQuestions() == null ? List.of()
                        : question.getSubQuestions().stream()
                                .map(CorrectionSubQuestionResponse::fromEntity)
                                .toList());
    }
}
