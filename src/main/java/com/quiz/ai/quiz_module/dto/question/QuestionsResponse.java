package com.quiz.ai.quiz_module.dto.question;

import java.util.List;

public record QuestionsResponse(
        List<QuestionSummaryResponse> questions) {
}
