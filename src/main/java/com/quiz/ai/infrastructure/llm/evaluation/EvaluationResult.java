package com.quiz.ai.infrastructure.llm.evaluation;

import lombok.Builder;
import lombok.Data;

/**
 * Represents the evaluation result of question corrections
 * Can be extended with additional evaluation metrics
 */
@Data
@Builder
public class EvaluationResult {
    private String evaluationId;
    private Double qualityScore; // 0.0 to 1.0
    private Boolean isValid;
    private String feedback;
    private Long evaluationTimeMs;
    private String evaluatorVersion;
}
