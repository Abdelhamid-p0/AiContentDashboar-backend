package com.quiz.ai.application.service;

import com.quiz.ai.application.dto.quiz.QuizzesByTypeResponse;

public interface QuizService {
    QuizzesByTypeResponse getQuizzesByCourseId(String courseId);
}
