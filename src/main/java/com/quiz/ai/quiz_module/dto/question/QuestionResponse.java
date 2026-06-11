package com.quiz.ai.quiz_module.dto.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.quiz_module.entity.question.Question;

import java.util.List;

public record QuestionResponse(
        String id,
        String question,
        @JsonProperty("question_type") String questionType,
        String image,
        String feedback,
        @JsonProperty("feedback_audio") String feedbackAudio,
        @JsonProperty("question_audio") String questionAudio,
        @JsonProperty("order_num") Integer orderNum,
        ObjectiveResponse objective,
        @JsonProperty("sub_questions") List<SubQuestionResponse> subQuestions) {
    public static QuestionResponse fromEntity(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getQuestion(),
                question.getQuestionType().name(),
                question.getImage(),
                question.getFeedback(),
                question.getFeedbackAudio(),
                question.getQuestionAudio(),
                question.getOrderNum(),
                question.getObjective() != null ? ObjectiveResponse.fromEntity(question.getObjective()) : null,
                question.getSubQuestions().stream()
                        .map(SubQuestionResponse::fromEntity)
                        .toList());
    }
}
