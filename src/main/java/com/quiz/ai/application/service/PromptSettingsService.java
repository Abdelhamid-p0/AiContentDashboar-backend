package com.quiz.ai.application.service;

import com.quiz.ai.application.dto.prompt.PromptSettingsRequest;
import com.quiz.ai.domains.prompt.PromptSettings;

public interface PromptSettingsService {
    PromptSettings getPromptSettings();

    PromptSettings createPromptSettings(PromptSettingsRequest request);

    PromptSettings updatePromptSettings(PromptSettingsRequest request);

    void deletePromptSettings();
}
