package com.quiz.ai.correctionModule.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.quiz.ai.correctionModule.dto.PromptSettingsRequest;
import com.quiz.ai.correctionModule.entity.PromptSettings;
import com.quiz.ai.correctionModule.repository.PromptSettingsRepository;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class PromptSettingsServiceImpl implements PromptSettingsService {

    private static final List<String> REQUIRED_SYSTEM_TOKENS = List.of("pedagogical_rules", "rag_rules_title",
            "rag_context");

    private static final List<String> REQUIRED_CORRECTION_TOKENS = List.of("question_json", "output_format");

    private static final List<String> REQUIRED_CHAT_TOKENS = List.of("instruction", "previous_correction_json",
            "question_json", "output_format");

    private static final String DEFAULT_PEDAGOGICAL_RULES = """
            - Ensure questions are clear and unambiguous
            - Ensure answers are correct
            - Keep correct answers unchanged unless necessary
            - Avoid making all answers true
            - Ensure pedagogical relevance
            """;

    private static final String DEFAULT_SYSTEM_MESSAGE_TEMPLATE = """
            You are an expert pedagogical assistant.

            TASK:
            - Correct educational questions
            - Respect pedagogical rules strictly
            - Improve clarity and accuracy

            =========================
            RAG INPUT (DO NOT IGNORE)
            =========================

            RULE TITLE (MUST BE COPIED EXACTLY):
            {{rag_rules_title}}

            RAG CONTEXT (MUST BE COPIED EXACTLY INTO OUTPUT):
            {{rag_context}}

            =========================
            CONTEXT INFO
            =========================
            Level: {{level}}
            Subject: {{subject}}
            Domain: {{domain}}
            Semester: {{semester}}

            Pedagogical Rules:
            {{pedagogical_rules}}

            =========================
            STRICT RULES
            =========================
            1. NEVER modify rag_rules_title
            2. ALWAYS copy rag_context into output field "context"
            3. NEVER invent missing fields
            4. Return ONLY valid JSON
            """;

    private static final String DEFAULT_CORRECTION_PROMPT_TEMPLATE = """
            Analyze and correct the question.

            QUESTION:
            {{question_json}}

            OUTPUT FORMAT:
            {{output_format}}

            Return ONLY JSON.
            """;

    private static final String DEFAULT_CHAT_PROMPT_TEMPLATE = """
            User instruction:
            {{instruction}}

            Previous correction:
            {{previous_correction_json}}

            Question:
            {{question_json}}

            Output format:
            {{output_format}}

            Return ONLY JSON.
            """;

    private final PromptSettingsRepository promptSettingsRepository;

    @Override
    public PromptSettings getPromptSettings() {
        return promptSettingsRepository.findTopByOrderByUpdatedAtDesc()
                .orElseGet(() -> promptSettingsRepository.save(buildDefaultSettings()));
    }

    @Override
    public PromptSettings createPromptSettings(PromptSettingsRequest request) {
        validateRequest(request);

        if (promptSettingsRepository.count() > 0) {
            throw new ResponseStatusException(CONFLICT, "Prompt settings already exist");
        }

        return promptSettingsRepository.save(buildFromRequest(request));
    }

    @Override
    public PromptSettings updatePromptSettings(PromptSettingsRequest request) {
        validateRequest(request);

        PromptSettings settings = promptSettingsRepository.findTopByOrderByUpdatedAtDesc()
                .orElseGet(this::buildDefaultSettings);

        applyRequest(settings, request);
        return promptSettingsRepository.save(settings);
    }

    @Override
    public void deletePromptSettings() {
        promptSettingsRepository.deleteAll();
    }

    private PromptSettings buildDefaultSettings() {
        PromptSettings settings = new PromptSettings();
        settings.setSystemMessageTemplate(DEFAULT_SYSTEM_MESSAGE_TEMPLATE);
        settings.setPedagogicalRules(DEFAULT_PEDAGOGICAL_RULES);
        settings.setCorrectionPromptTemplate(DEFAULT_CORRECTION_PROMPT_TEMPLATE);
        settings.setChatPromptTemplate(DEFAULT_CHAT_PROMPT_TEMPLATE);
        return settings;
    }

    private PromptSettings buildFromRequest(PromptSettingsRequest request) {
        PromptSettings settings = new PromptSettings();
        applyRequest(settings, request);
        return settings;
    }

    private void applyRequest(PromptSettings settings, PromptSettingsRequest request) {
        settings.setSystemMessageTemplate(request.systemMessageTemplate());
        settings.setPedagogicalRules(request.pedagogicalRules());
        settings.setCorrectionPromptTemplate(request.correctionPromptTemplate());
        settings.setChatPromptTemplate(request.chatPromptTemplate());
    }

    private void validateRequest(PromptSettingsRequest request) {
        requireText(request.systemMessageTemplate(), "system_message_template");
        requireText(request.pedagogicalRules(), "pedagogical_rules");
        requireText(request.correctionPromptTemplate(), "correction_prompt_template");
        requireText(request.chatPromptTemplate(), "chat_prompt_template");
    }

    private void requireText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " is required");
        }
    }
}