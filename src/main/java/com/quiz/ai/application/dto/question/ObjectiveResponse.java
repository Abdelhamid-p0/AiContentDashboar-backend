package com.quiz.ai.application.dto.question;

import com.quiz.ai.domains.question.Objective;

public record ObjectiveResponse(
        String id,
        String objective) {
    public static ObjectiveResponse fromEntity(Objective objective) {
        return new ObjectiveResponse(
                objective.getId(),
                objective.getObjective());
    }
}
