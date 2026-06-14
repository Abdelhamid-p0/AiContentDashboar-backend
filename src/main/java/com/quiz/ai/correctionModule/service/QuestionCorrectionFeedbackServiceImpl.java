package com.quiz.ai.correctionModule.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionFeedbackRequest;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionFeedbackResponse;
import com.quiz.ai.correctionModule.entity.QuestionCorrectionFeedback;
import com.quiz.ai.correctionModule.repository.QuestionCorrectionFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionCorrectionFeedbackServiceImpl implements QuestionCorrectionFeedbackService {
    private final QuestionCorrectionFeedbackRepository questionCorrectionFeedbackRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public QuestionCorrectionFeedbackResponse submitFeedback(String questionId, QuestionCorrectionFeedbackRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Feedback payload is required");
        }

        if (request.rating() == null || request.rating() < 1 || request.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        if (request.correction() == null) {
            throw new IllegalArgumentException("Correction snapshot is required");
        }

        String normalizedComment = request.comment() == null || request.comment().isBlank()
                ? null
                : request.comment().trim();

        String correctionSnapshot;
        try {
            correctionSnapshot = objectMapper.writeValueAsString(request.correction());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize correction snapshot", e);
        }

        QuestionCorrectionFeedback feedback = QuestionCorrectionFeedback.builder()
                .questionId(questionId)
                .rating(request.rating())
                .comment(normalizedComment)
                .correctionSnapshot(correctionSnapshot)
                .build();

        QuestionCorrectionFeedback savedFeedback = questionCorrectionFeedbackRepository.save(feedback);
        return QuestionCorrectionFeedbackResponse.fromEntity(savedFeedback);
    }
}