package com.quiz.ai.quizModule.service;

import com.quiz.ai.quizModule.dto.question.QuestionResponse;
import com.quiz.ai.quizModule.dto.question.QuestionCorrectionContextResponse;
import com.quiz.ai.quizModule.dto.question.QuestionsResponse;

public interface QuestionService {
    QuestionsResponse getQuestionsByQuizId(String quizId);

    QuestionResponse getQuestionById(String questionId);

    QuestionCorrectionContextResponse getQuestionCorrectionContext(String questionId);
}
