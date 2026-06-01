package com.quiz.ai.application.dto.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quiz.ai.domains.prompt.PromptSettings;

import java.time.Instant;

public record PromptSettingsResponse(
        @JsonProperty("id") String id,
        @JsonProperty("system_message_template") String systemMessageTemplate,
        @JsonProperty("pedagogical_rules") String pedagogicalRules,
        @JsonProperty("correction_prompt_template") String correctionPromptTemplate,
        @JsonProperty("chat_prompt_template") String chatPromptTemplate,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt) {

    public static PromptSettingsResponse fromEntity(PromptSettings settings) {
        return new PromptSettingsResponse(
                settings.getId(),
                settings.getSystemMessageTemplate(),
                settings.getPedagogicalRules(),
                settings.getCorrectionPromptTemplate(),
                settings.getChatPromptTemplate(),
                settings.getCreatedAt(),
                settings.getUpdatedAt());
    }
}
