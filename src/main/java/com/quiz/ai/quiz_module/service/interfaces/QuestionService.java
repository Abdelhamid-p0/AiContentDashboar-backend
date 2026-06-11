package com.quiz.ai.quiz_module.service.interfaces;

import com.quiz.ai.quiz_module.dto.question.QuestionResponse;
import com.quiz.ai.quiz_module.dto.question.QuestionsResponse;

public interface QuestionService {
    QuestionsResponse getQuestionsByQuizId(String quizId);

    QuestionResponse getQuestionById(String questionId);
}
