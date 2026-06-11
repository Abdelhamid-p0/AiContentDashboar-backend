package com.quiz.ai.quizModule.dto.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.quizModule.entity.question.SubQuestion;

import java.util.List;

public record SubQuestionResponse(
        String id,
        String question,
        @JsonProperty("order_num") Integer orderNum,
        List<AnswerResponse> answers) {
    public static SubQuestionResponse fromEntity(SubQuestion subQuestion) {
        return new SubQuestionResponse(
                subQuestion.getId(),
                subQuestion.getQuestion(),
                subQuestion.getOrderNum(),
                subQuestion.getAnswers().stream()
                        .map(AnswerResponse::fromEntity)
                        .toList());
    }
}
