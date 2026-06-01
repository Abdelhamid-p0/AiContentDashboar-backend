package com.quiz.ai.interfaces.rest;

import com.quiz.ai.application.dto.prompt.PromptSettingsRequest;
import com.quiz.ai.application.dto.prompt.PromptSettingsResponse;
import com.quiz.ai.application.service.PromptSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/prompt-settings")
@RequiredArgsConstructor
public class PromptSettingsController {
    private final PromptSettingsService promptSettingsService;

    @GetMapping
    public PromptSettingsResponse getPromptSettings() {
        return PromptSettingsResponse.fromEntity(promptSettingsService.getPromptSettings());
    }

    @PostMapping
    public PromptSettingsResponse createPromptSettings(@RequestBody PromptSettingsRequest request) {
        return PromptSettingsResponse.fromEntity(promptSettingsService.createPromptSettings(request));
    }

    @PutMapping
    public PromptSettingsResponse updatePromptSettings(@RequestBody PromptSettingsRequest request) {
        return PromptSettingsResponse.fromEntity(promptSettingsService.updatePromptSettings(request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePromptSettings() {
        promptSettingsService.deletePromptSettings();
        return ResponseEntity.noContent().build();
    }
}
