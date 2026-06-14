package com.quiz.ai.correctionModule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.quiz.ai.correctionModule.dto.PromptSettingsRequest;
import com.quiz.ai.correctionModule.entity.PromptSettings;
import com.quiz.ai.correctionModule.repository.PromptSettingsRepository;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptSettingsServiceImpl implements PromptSettingsService {

    private static final String DEFAULT_GENERAL_RULES = """
            - Ensure clarity and unambiguous questions
            - Ensure answers are consistent with course, level, and domain
            - Ensure pedagogical correctness
            - Avoid introducing knowledge outside curriculum
            - respect strictly the rag_context as pedagogical source of truth
            """;

    // =========================
    // SYSTEM PROMPT (OPTIMIZED)
    // =========================
    private static final String DEFAULT_SYSTEM_MESSAGE_TEMPLATE = """
            You are an expert pedagogical AI system.

            =========================
            ROLE
            =========================
            You are NOT a chatbot.
            You are a deterministic JSON generator.

            =========================
            TASK
            =========================
            - Analyze educational questions
            - Detect inconsistencies or errors
            - Improve pedagogy and clarity
            - Generate missing fields ONLY when necessary

            =========================
            OUTPUT CONTRACT (STRICT)
            =========================
            You MUST return ONLY valid JSON matching:
            QuestionCorrectionResponse.class

            Rules:
            - No extra fields
            - No missing fields
            - Always valid JSON


            =========================
            GENERAL RULES
            =========================
            {{general_rules}}


            =========================
            CONTEXT (DO NOT MODIFY)
            =========================
            rag_context:
            {{rag_context}}


            =========================
            MULTIPLE CHOICE RULE (CRITICAL)
            =========================
            - Answers are NOT fixed: they may be corrected ONLY if clearly incorrect
            - Never randomly flip answers
            - Never modify correct answers without strong justification
            - Always preserve valid correct answers
            - Corrections must be based on course, level, and context

            =========================
            FINAL RULE
            =========================
            Return ONLY valid JSON matching QuestionCorrectionResponse.class
            """;

    // =========================
    // CORRECTION PROMPT
    // =========================
    private static final String DEFAULT_CORRECTION_PROMPT_TEMPLATE = """
            Correct the following educational question.

            =========================
            INPUT
            =========================
            {{question_json}}


            =========================
            OUTPUT CONTRACT
            =========================
            Return ONLY JSON matching:
            QuestionCorrectionResponse.class
            """;

    // =========================
    // CHAT PROMPT
    // =========================
    private static final String DEFAULT_CHAT_PROMPT_TEMPLATE = """
            User instruction:
            {{instruction}}

            Context:
            {{instruction_context}}

            Previous correction:
            {{previous_correction_json}}

            Question:
            {{question_json}}


            =========================
            OUTPUT CONTRACT
            =========================
            Return ONLY JSON matching QuestionCorrectionResponse.class


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
        log.info("Deleting all prompt settings");
        promptSettingsRepository.deleteAll();
    }

    private PromptSettings buildDefaultSettings() {
        PromptSettings settings = new PromptSettings();
        settings.setSystemMessageTemplate(DEFAULT_SYSTEM_MESSAGE_TEMPLATE);
        settings.setGeneralRules(DEFAULT_GENERAL_RULES);
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
        settings.setGeneralRules(request.generalRules());
        settings.setCorrectionPromptTemplate(request.correctionPromptTemplate());
        settings.setChatPromptTemplate(request.chatPromptTemplate());
    }

    private void validateRequest(PromptSettingsRequest request) {
        requireText(request.systemMessageTemplate(), "system_message_template");
        requireText(request.generalRules(), "general_rules");
        requireText(request.correctionPromptTemplate(), "correction_prompt_template");
        requireText(request.chatPromptTemplate(), "chat_prompt_template");
    }

    private void requireText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " is required");
        }
    }
}