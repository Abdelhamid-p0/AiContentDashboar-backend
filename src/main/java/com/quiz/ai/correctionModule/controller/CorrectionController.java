package com.quiz.ai.correctionModule.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.ai.correctionModule.dto.QuestionCorrectionChatRequest;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionFeedbackRequest;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionFeedbackResponse;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionResponse;
import com.quiz.ai.correctionModule.service.CorrectionService;
import com.quiz.ai.correctionModule.service.QuestionCorrectionFeedbackService;

@RestController
@RequestMapping("/api/v1/questions/{questionId}/correct")
@RequiredArgsConstructor
public class CorrectionController {
    private final CorrectionService correctionService;
    private final QuestionCorrectionFeedbackService questionCorrectionFeedbackService;

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

    @PostMapping("/feedback")
    public ResponseEntity<QuestionCorrectionFeedbackResponse> submitFeedback(
            @PathVariable String questionId,
            @Valid @RequestBody QuestionCorrectionFeedbackRequest request) {
        QuestionCorrectionFeedbackResponse response = questionCorrectionFeedbackService.submitFeedback(
                questionId,
                request);
        return ResponseEntity.ok(response);
    }
}

/*
 * A faire :
 * - dto de question (utilser selement les champs qui sont necessaires pour la
 * correction) X
 * - dto de question à envoyer au systeme rag X
 * 
 * - découpler les modules X
 * - décomposer le module correction 
 * 
 * - Afficher le context dans le front X
 * - 5 étoile d'évaluation + feedback  X
 * - Agent orchestrateur global
 * 
 */