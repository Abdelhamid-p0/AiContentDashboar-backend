package com.quiz.ai.prompt_builder_module.service;

import com.quiz.ai.prompt_builder_module.dto.prompt.PromptSettingsRequest;
import com.quiz.ai.prompt_builder_module.model.prompt.PromptSettings;

public interface PromptSettingsService {
    PromptSettings getPromptSettings();

    PromptSettings createPromptSettings(PromptSettingsRequest request);

    PromptSettings updatePromptSettings(PromptSettingsRequest request);

    void deletePromptSettings();
}
