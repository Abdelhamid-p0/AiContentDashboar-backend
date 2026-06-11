package com.quiz.ai.quizModule.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.ai.quizModule.dto.question.QuestionResponse;
import com.quiz.ai.quizModule.service.QuestionService;

@RestController
@RequestMapping("/api/v1/questions/{questionId}")
@RequiredArgsConstructor
public class QuestionDetailsController {
    private final QuestionService questionService;

    @GetMapping
    public QuestionResponse getQuestionDetails(@PathVariable String questionId) {
        return questionService.getQuestionById(questionId);
    }
}
