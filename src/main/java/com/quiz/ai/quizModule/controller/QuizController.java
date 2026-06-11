package com.quiz.ai.quizModule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.ai.quizModule.dto.quiz.QuizzesByTypeResponse;
import com.quiz.ai.quizModule.service.QuizService;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/quizzes")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public QuizzesByTypeResponse getQuizzesByCourse(@PathVariable String courseId) {
        return quizService.getQuizzesByCourseId(courseId);
    }
}
