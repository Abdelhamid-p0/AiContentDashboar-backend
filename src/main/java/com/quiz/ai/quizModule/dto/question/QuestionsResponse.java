package com.quiz.ai.quizModule.dto.question;

import java.util.List;

public record QuestionsResponse(
        List<QuestionSummaryResponse> questions) {
}
