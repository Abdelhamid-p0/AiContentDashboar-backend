package com.quiz.ai.infrastructure.llm.evaluation;

import com.quiz.ai.application.dto.correction.QuestionCorrectionResponse;

/**
 * Interface for evaluating LLM correction results
 * Can be extended with different evaluation strategies (scoring, validation,
 * etc.)
 * Allows for future implementation of ML-based evaluation
 */
public interface CorrectionsEvaluator {
    /**
     * Evaluate the quality of corrections
     * 
     * @param response the correction response to evaluate
     * @return evaluation score or metadata
     */
    EvaluationResult evaluate(QuestionCorrectionResponse response);

    /**
     * Check if corrections meet minimum quality threshold
     * 
     * @param response the correction response
     * @return true if quality is acceptable
     */
    boolean isQualityAcceptable(QuestionCorrectionResponse response);
}
