package com.quiz.ai.correctionModule.service;

import com.quiz.ai.correctionModule.dto.PromptSettingsRequest;
import com.quiz.ai.correctionModule.entity.PromptSettings;

public interface PromptSettingsService {
    PromptSettings getPromptSettings();

    PromptSettings createPromptSettings(PromptSettingsRequest request);

    PromptSettings updatePromptSettings(PromptSettingsRequest request);

    void deletePromptSettings();
}
