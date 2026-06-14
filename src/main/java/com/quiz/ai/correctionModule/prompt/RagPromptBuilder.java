package com.quiz.ai.correctionModule.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionResponse;
import com.quiz.ai.correctionModule.dto.RagContextDocumentResponse;
import com.quiz.ai.correctionModule.service.PromptSettingsService;
import com.quiz.ai.quizModule.dto.course.CourseCorrectionContextResponse;
import com.quiz.ai.quizModule.dto.question.CorrectionQuestionResponse;
import com.quiz.ai.quizModule.dto.question.CorrectionSubQuestionResponse;
import com.quiz.ai.ragModule.service.retrieval.PedagogicalRagService;
import com.quiz.ai.ragModule.service.retrieval.UserInstructionRagService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RagPromptBuilder implements PromptBuilder {

        private final ObjectMapper objectMapper;
        private final PromptSettingsService promptSettingsService;
        private final PedagogicalRagService pedagogicalRagService;
        private final UserInstructionRagService userInstructionRagService;

        @Override
        public SystemPromptResult buildSystemMessage(CourseCorrectionContextResponse course) {

                var settings = promptSettingsService.getPromptSettings();

                var ragContext = pedagogicalRagService.retrieveContext(
                                course.level(),
                                course.subject(),
                                course.domain());
                String promptRagContext = ragContext.toPromptContext();

                log.info("=== RAG CONTEXT ===\n{}", promptRagContext);

                Map<String, String> tokens = new LinkedHashMap<>();

                tokens.put("level", course.level());
                tokens.put("subject", course.subject());
                tokens.put("domain", course.domain());
                tokens.put("semester", course.semester());

                tokens.put("general_rules", settings.getGeneralRules());

                tokens.put("rag_context", promptRagContext);

                String systemPrompt = renderTemplate(settings.getSystemMessageTemplate(), tokens);

                log.info("=== FINAL SYSTEM PROMPT ===\n{}", systemPrompt);

                return new SystemPromptResult(
                                systemPrompt,
                                ragContext.documents().stream()
                                                .map(RagContextDocumentResponse::fromRagDocument)
                                                .toList());
        }

        @Override
        public String buildQuestionCorrectionPrompt(
                        CorrectionQuestionResponse question,
                        CourseCorrectionContextResponse course) {

                var settings = promptSettingsService.getPromptSettings();

                String questionJson = toJson(buildQuestionPayload(question));

                Map<String, String> tokens = new LinkedHashMap<>();
                tokens.put("question_json", questionJson);

                String prompt = renderTemplate(settings.getCorrectionPromptTemplate(), tokens);

                log.info("=== QUESTION PROMPT ===\n{}", prompt);

                return prompt;
        }

        @Override
        public String buildQuestionCorrectionChatPrompt(
                        CorrectionQuestionResponse question,
                        CourseCorrectionContextResponse course,
                        QuestionCorrectionResponse previousCorrection,
                        String userMessage) {

                var settings = promptSettingsService.getPromptSettings();

                String questionJson = toJson(buildQuestionPayload(question));
                String previousCorrectionJson = previousCorrection == null ? "null" : toJson(previousCorrection);

                String instructionContext = userInstructionRagService.retrieveContext(userMessage);

                Map<String, String> tokens = new LinkedHashMap<>();

                tokens.put("instruction", userMessage == null ? "" : userMessage);
                tokens.put("instruction_context", instructionContext);
                tokens.put("previous_correction_json", previousCorrectionJson);
                tokens.put("question_json", questionJson);

                String prompt = renderTemplate(settings.getChatPromptTemplate(), tokens);

                log.info("=== CHAT PROMPT ===\n{}", prompt);

                return prompt;
        }

        private String renderTemplate(String template, Map<String, String> tokens) {
                String resolved = template;

                for (Map.Entry<String, String> entry : tokens.entrySet()) {
                        String value = entry.getValue() == null ? "" : entry.getValue();

                        resolved = resolved.replace(
                                        "{{" + entry.getKey() + "}}",
                                        value);
                }

                return resolved;
        }

        private String toJson(Object object) {
                try {
                        return objectMapper.writeValueAsString(object);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        private Map<String, Object> buildQuestionPayload(CorrectionQuestionResponse question) {
                Map<String, Object> payload = new LinkedHashMap<>();

                payload.put("id", question.id());
                payload.put("question", question.question());
                payload.put("question_type", question.questionType());
                payload.put("feedback", question.feedback());
                payload.put("objective", question.objective());

                payload.put(
                                "sub_questions",
                                question.subQuestions() == null
                                                ? List.of()
                                                : question.subQuestions().stream()
                                                                .map(this::buildSubQuestionPayload)
                                                                .toList());

                return payload;
        }

        private Map<String, Object> buildSubQuestionPayload(CorrectionSubQuestionResponse sq) {
                Map<String, Object> payload = new LinkedHashMap<>();

                payload.put("id", sq.id());
                payload.put("question", sq.question());

                return payload;
        }
}
