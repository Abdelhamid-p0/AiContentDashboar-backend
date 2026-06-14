package com.quiz.ai.correctionModule.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PromptSettingsRequest(
        @JsonProperty("system_message_template") String systemMessageTemplate,
        @JsonProperty("general_rules") String generalRules,
        @JsonProperty("correction_prompt_template") String correctionPromptTemplate,
        @JsonProperty("chat_prompt_template") String chatPromptTemplate) {
}
