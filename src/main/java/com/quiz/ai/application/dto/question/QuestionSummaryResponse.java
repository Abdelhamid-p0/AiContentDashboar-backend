package com.quiz.ai.application.dto.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.domains.question.Question;

public record QuestionSummaryResponse(
        String id,
        String question,
        @JsonProperty("question_type") String questionType,
        @JsonProperty("order_num") Integer orderNum,
        ObjectiveResponse objective) {

    public static QuestionSummaryResponse fromEntity(Question question) {
        return new QuestionSummaryResponse(
                question.getId(),
                question.getQuestion(),
                question.getQuestionType() == null ? null : question.getQuestionType().name(),
                question.getOrderNum(),
                question.getObjective() != null ? ObjectiveResponse.fromEntity(question.getObjective()) : null);
    }
}
