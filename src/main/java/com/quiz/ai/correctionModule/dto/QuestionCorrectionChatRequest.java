package com.quiz.ai.correctionModule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuestionCorrectionChatRequest(
                @JsonProperty("user_message") String userMessage,
                @JsonProperty("previous_correction") QuestionCorrectionResponse previousCorrection) {
}
