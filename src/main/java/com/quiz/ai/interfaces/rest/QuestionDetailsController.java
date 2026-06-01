package com.quiz.ai.interfaces.rest;

import com.quiz.ai.application.dto.question.QuestionResponse;
import com.quiz.ai.application.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
