package com.quiz.ai.correctionModule.service;

import com.quiz.ai.correctionModule.dto.QuestionCorrectionResponse;

public interface CorrectionService {
    /**
     * Correct a question using LLM
     * 
     * @param questionId the question to correct
     * @return the corrected question
     */
    QuestionCorrectionResponse correctQuestion(String questionId);

    /**
     * Correct a question using user personalization and the previous AI result
     * 
     * @param questionId         the question to correct
     * @param userMessage        the user personalization request
     * @param previousCorrection the previous AI correction result
     * @return the corrected question
     */
    QuestionCorrectionResponse correctQuestionWithInstruction(
            String questionId,
            String userMessage,
            QuestionCorrectionResponse previousCorrection);
}
