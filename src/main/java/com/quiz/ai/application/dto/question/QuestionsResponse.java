package com.quiz.ai.application.dto.question;

import java.util.List;

public record QuestionsResponse(
                List<QuestionSummaryResponse> questions) {
}
