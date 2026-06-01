package com.quiz.ai.application.dto.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PromptSettingsRequest(
        @JsonProperty("system_message_template") String systemMessageTemplate,
        @JsonProperty("pedagogical_rules") String pedagogicalRules,
        @JsonProperty("correction_prompt_template") String correctionPromptTemplate,
        @JsonProperty("chat_prompt_template") String chatPromptTemplate) {
}
