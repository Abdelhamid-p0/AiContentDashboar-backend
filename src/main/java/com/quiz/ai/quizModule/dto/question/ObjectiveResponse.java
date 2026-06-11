package com.quiz.ai.quizModule.dto.question;

import com.quiz.ai.quizModule.entity.question.Objective;

public record ObjectiveResponse(
        String id,
        String objective) {
    public static ObjectiveResponse fromEntity(Objective objective) {
        return new ObjectiveResponse(
                objective.getId(),
                objective.getObjective());
    }
}
