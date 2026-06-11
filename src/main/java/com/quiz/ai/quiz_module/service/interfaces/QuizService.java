package com.quiz.ai.quiz_module.service.interfaces;

import com.quiz.ai.quiz_module.dto.quiz.QuizzesByTypeResponse;

public interface QuizService {
    QuizzesByTypeResponse getQuizzesByCourseId(String courseId);
}
