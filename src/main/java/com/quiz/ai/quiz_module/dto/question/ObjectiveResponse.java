package com.quiz.ai.quiz_module.dto.question;

import com.quiz.ai.quiz_module.entity.question.Objective;

public record ObjectiveResponse(
        String id,
        String objective) {
    public static ObjectiveResponse fromEntity(Objective objective) {
        return new ObjectiveResponse(
                objective.getId(),
                objective.getObjective());
    }
}
