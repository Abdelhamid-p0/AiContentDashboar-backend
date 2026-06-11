package com.quiz.ai.correctionModule.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.ai.correctionModule.dto.QuestionCorrectionChatRequest;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionResponse;
import com.quiz.ai.correctionModule.service.CorrectionService;

@RestController
@RequestMapping("/api/v1/questions/{questionId}/correct")
@RequiredArgsConstructor
public class CorrectionController {
    private final CorrectionService correctionService;

    @GetMapping
    public ResponseEntity<QuestionCorrectionResponse> correctQuestion(@PathVariable String questionId) {
        QuestionCorrectionResponse response = correctionService.correctQuestion(questionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat")
    public ResponseEntity<QuestionCorrectionResponse> personalizeCorrection(
            @PathVariable String questionId,
            @RequestBody QuestionCorrectionChatRequest request) {
        QuestionCorrectionResponse response = correctionService.correctQuestionWithInstruction(
                questionId,
                request.userMessage(),
                request.previousCorrection());
        return ResponseEntity.ok(response);
    }
}
