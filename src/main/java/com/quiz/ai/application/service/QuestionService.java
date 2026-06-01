package com.quiz.ai.application.service;

import com.quiz.ai.application.dto.question.QuestionsResponse;
import com.quiz.ai.application.dto.question.QuestionResponse;

public interface QuestionService {
    QuestionsResponse getQuestionsByQuizId(String quizId);

    QuestionResponse getQuestionById(String questionId);
}
