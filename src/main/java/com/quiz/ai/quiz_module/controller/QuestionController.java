package com.quiz.ai.quiz_module.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.ai.quiz_module.dto.question.QuestionsResponse;
import com.quiz.ai.quiz_module.service.interfaces.QuestionService;

@RestController
@RequestMapping("/api/v1/quizzes/{quizId}/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping
    public QuestionsResponse getQuestions(@PathVariable String quizId) {
        return questionService.getQuestionsByQuizId(quizId);
    }
}
