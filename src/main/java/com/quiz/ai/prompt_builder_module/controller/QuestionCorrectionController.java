package com.quiz.ai.prompt_builder_module.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.ai.prompt_builder_module.dto.correction.QuestionCorrectionChatRequest;
import com.quiz.ai.prompt_builder_module.dto.correction.QuestionCorrectionResponse;
import com.quiz.ai.prompt_builder_module.service.QuestionCorrectionService;

@RestController
@RequestMapping("/api/v1/questions/{questionId}/correct")
@RequiredArgsConstructor
public class QuestionCorrectionController {
    private final QuestionCorrectionService questionCorrectionService;

    @GetMapping
    public ResponseEntity<QuestionCorrectionResponse> correctQuestion(@PathVariable String questionId) {
        QuestionCorrectionResponse response = questionCorrectionService.correctQuestion(questionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat")
    public ResponseEntity<QuestionCorrectionResponse> personalizeCorrection(
            @PathVariable String questionId,
            @RequestBody QuestionCorrectionChatRequest request) {
        QuestionCorrectionResponse response = questionCorrectionService.correctQuestionWithInstruction(
                questionId,
                request.userMessage(),
                request.previousCorrection());
        return ResponseEntity.ok(response);
    }
}
