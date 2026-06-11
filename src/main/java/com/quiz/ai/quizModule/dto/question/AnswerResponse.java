package com.quiz.ai.quizModule.dto.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.quizModule.entity.question.Answer;

public record AnswerResponse(
        String id,
        String answer,
        String image,
        @JsonProperty("is_right") boolean isRight,
        @JsonProperty("answer_audio") String answerAudio,
        @JsonProperty("order_num") Integer orderNum) {
    public static AnswerResponse fromEntity(Answer answer) {
        return new AnswerResponse(
                answer.getId(),
                answer.getAnswer(),
                answer.getImage(),
                answer.isRight(),
                answer.getAnswerAudio(),
                answer.getOrderNum());
    }
}
