package com.quiz.ai.correctionModule.service;

import com.quiz.ai.correctionModule.dto.QuestionCorrectionFeedbackRequest;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionFeedbackResponse;

public interface QuestionCorrectionFeedbackService {
    QuestionCorrectionFeedbackResponse submitFeedback(String questionId, QuestionCorrectionFeedbackRequest request);
}