package com.quiz.ai.quizModule.service;

import com.quiz.ai.quizModule.dto.quiz.QuizzesByTypeResponse;

public interface QuizService {
    QuizzesByTypeResponse getQuizzesByCourseId(String courseId);
}
