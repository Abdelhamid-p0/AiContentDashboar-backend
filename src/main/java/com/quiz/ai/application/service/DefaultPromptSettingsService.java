package com.quiz.ai.application.service;

import com.quiz.ai.application.dto.prompt.PromptSettingsRequest;
import com.quiz.ai.domains.prompt.PromptSettings;
import com.quiz.ai.repository.PromptSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

@Service
@RequiredArgsConstructor
public class DefaultPromptSettingsService implements PromptSettingsService {
    private static final List<String> REQUIRED_SYSTEM_TOKENS = List.of("pedagogical_rules");
    private static final List<String> REQUIRED_CORRECTION_TOKENS = List.of("question_json", "output_format");
    private static final List<String> REQUIRED_CHAT_TOKENS = List.of(
            "instruction",
            "previous_correction_json",
            "question_json",
            "output_format");

    private static final String DEFAULT_PEDAGOGICAL_RULES = """
            - Ensure questions are clear and unambiguous
            - Provide feedback that is encouraging and constructive
            - Avoid ambiguous or tricky wording
            - Ensure all answers are grammatically correct
            - Check that the correct answer is actually correct
            - Preserve any answer that is already correct; do not rewrite correct options just to make the text look different
            - If the question contains one correct option and several false options, keep the correct option intact and only fix the false options when they are wrong or unclear
            - Do not \"correct\" every answer by making them all true; each incorrect distractor should stay incorrect unless it is truly invalid
            - Ensure difficulty is appropriate for the level
            - Make questions engaging and relevant to students
            - Provide explanations for pedagogical value
            """;

    private static final String DEFAULT_SYSTEM_MESSAGE_TEMPLATE = """
            You are an expert pedagogical assistant specialized in evaluating and improving educational questions.

            Your role is to:
            1. Review questions for clarity and correctness
            2. Ensure questions are appropriate for the student level
            3. Validate that all answers are correct and unambiguous
            4. Improve feedback and explanations
            5. Enhance pedagogical value

            Context Information:
            - Level: {{level}}
            - Subject: {{subject}}
            - Domain: {{domain}}
            - Semester: {{semester}}

            Pedagogical Rules to Follow:
            {{pedagogical_rules}}

            When correcting, maintain the structure and format of the original question.
            """;

    private static final String DEFAULT_CORRECTION_PROMPT_TEMPLATE = """
            Please review and correct the following question:

            QUESTION DATA (JSON):
            {{question_json}}

            TASK:
            1. Analyze the question for pedagogical quality
            2. Check all answers for correctness
            3. Identify only the answers that are actually wrong, unclear, or badly written
            4. Keep already-correct answers unchanged unless a minimal wording fix is required
            5. Maintain the original structure, especially the number of answers and their correctness flags
            6. If the question already contains one true answer, keep that true answer true and do not turn every choice into a true choice
            7. Explain what was corrected and why

            OUTPUT FORMAT (return valid JSON):
            {{output_format}}

            Ensure your response is valid JSON that can be parsed.
            Return ONLY the JSON, no additional text.
            """;

    private static final String DEFAULT_CHAT_PROMPT_TEMPLATE = """
            The user wants to personalize the AI correction. Treat their instruction as the PRIMARY request.

            USER INSTRUCTION (PRIMARY):
            {{instruction}}

            PREVIOUS AI CORRECTION (JSON):
            {{previous_correction_json}}

            ORIGINAL QUESTION DATA (JSON):
            {{question_json}}

            TASK:
            1. Apply the user instruction while respecting the pedagogical rules
            2. Use the previous correction as a baseline, adjusting only what is needed
            3. Keep the original structure and correctness flags intact
            4. Update corrections, explanation, and detected_errors to reflect the new result

            OUTPUT FORMAT (return valid JSON):
            {{output_format}}

            Ensure your response is valid JSON that can be parsed.
            Return ONLY the JSON, no additional text.
            """;

    private final PromptSettingsRepository promptSettingsRepository;

    @Override
    public PromptSettings getPromptSettings() {
        return promptSettingsRepository
                .findTopByOrderByUpdatedAtDesc()
                .orElseGet(() -> promptSettingsRepository.save(buildDefaultSettings()));
    }

    @Override
    public PromptSettings createPromptSettings(PromptSettingsRequest request) {
        validateRequest(request);

        if (promptSettingsRepository.count() > 0) {
            throw new ResponseStatusException(CONFLICT, "Prompt settings already exist");
        }

        PromptSettings settings = buildFromRequest(request);
        return promptSettingsRepository.save(settings);
    }

    @Override
    public PromptSettings updatePromptSettings(PromptSettingsRequest request) {
        validateRequest(request);

        PromptSettings settings = promptSettingsRepository
                .findTopByOrderByUpdatedAtDesc()
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

        validateTokens(request.systemMessageTemplate(), "system_message_template", REQUIRED_SYSTEM_TOKENS);
        validateTokens(request.correctionPromptTemplate(), "correction_prompt_template", REQUIRED_CORRECTION_TOKENS);
        validateTokens(request.chatPromptTemplate(), "chat_prompt_template", REQUIRED_CHAT_TOKENS);
    }

    private void requireText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new ResponseStatusException(BAD_REQUEST, fieldName + " is required");
        }
    }

    private void validateTokens(String template, String fieldName, List<String> requiredTokens) {
        List<String> missingTokens = requiredTokens.stream()
                .filter(token -> !containsToken(template, token))
                .map(token -> "{{" + token + "}}")
                .toList();

        if (!missingTokens.isEmpty()) {
            throw new ResponseStatusException(
                    BAD_REQUEST,
                    fieldName + " missing required tokens: " + String.join(", ", missingTokens));
        }
    }

    private boolean containsToken(String template, String token) {
        return template != null && template.contains("{{" + token + "}}");
    }
}
