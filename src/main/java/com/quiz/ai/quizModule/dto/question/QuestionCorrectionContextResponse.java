package com.quiz.ai.quizModule.dto.question;

import com.quiz.ai.quizModule.dto.course.CourseCorrectionContextResponse;
import com.quiz.ai.quizModule.entity.question.Question;

public record QuestionCorrectionContextResponse(
        CorrectionQuestionResponse question,
        CourseCorrectionContextResponse course) {
    public static QuestionCorrectionContextResponse fromEntity(Question question) {
        return new QuestionCorrectionContextResponse(
                CorrectionQuestionResponse.fromEntity(question),
                CourseCorrectionContextResponse.fromEntity(question.getQuiz().getCourse()));
    }
}
