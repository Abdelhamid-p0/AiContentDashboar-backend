package com.quiz.ai.prompt_builder_module.dto.correction;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuestionCorrectionChatRequest(
                @JsonProperty("user_message") String userMessage,
                @JsonProperty("previous_correction") QuestionCorrectionResponse previousCorrection) {
}
