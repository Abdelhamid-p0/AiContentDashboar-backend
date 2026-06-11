package com.quiz.ai.correctionModule.prompt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionResponse;
import com.quiz.ai.correctionModule.service.PromptSettingsService;
import com.quiz.ai.quizModule.entity.question.Answer;
import com.quiz.ai.quizModule.entity.question.Question;
import com.quiz.ai.quizModule.entity.question.SubQuestion;
import com.quiz.ai.quizModule.entity.subject.Course;
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

        // âœ… STRUCTURE PLUS ROBUSTE
        private static final String OUTPUT_FORMAT = """
                        {
                          "context": {
                            "rag_rules_title": "MUST BE COPIED EXACTLY",
                            "rag_context": "MUST BE COPIED EXACTLY"
                          },

                          "meta": {
                            "title": "",
                            "summary": "",
                            "difficulty_reasoning": ""
                          },

                          "corrections": [],
                          "explanation": "",
                          "detected_errors": "",

                          "improved_question": {
                            "id": "",
                            "question": "",
                            "question_type": "",
                            "image": null,
                            "feedback": null,
                            "feedback_audio": null,
                            "question_audio": null,
                            "order_num": null,

                            "objective": {
                              "id": "",
                              "objective": ""
                            },

                            "sub_questions": []
                          }
                        }
                        """;

        @Override
        public String buildSystemMessage(Course course) {

                var settings = promptSettingsService.getPromptSettings();

                String ragContext = pedagogicalRagService.retrieveContext(
                                course.getLevel().getLevelName(),
                                course.getSubject().getTitle(),
                                course.getDomain().getTitle(),
                                course.getSemester().name());

                String ragRulesTitle = "DEFAULT_PEDAGOGICAL_RULES";

                log.info("=== RAG CONTEXT ===\n{}", ragContext);
                log.info("=== RAG TITLE === {}", ragRulesTitle);

                Map<String, String> tokens = new LinkedHashMap<>();

                tokens.put("level", course.getLevel().getLevelName());
                tokens.put("subject", course.getSubject().getTitle());
                tokens.put("domain", course.getDomain().getTitle());
                tokens.put("semester", course.getSemester().name());

                tokens.put("pedagogical_rules", settings.getPedagogicalRules());

                // ðŸ”¥ CRITICAL
                tokens.put("rag_context", ragContext);
                tokens.put("rag_rules_title", ragRulesTitle);

                String systemPrompt = renderTemplate(settings.getSystemMessageTemplate(), tokens);

                log.info("=== FINAL SYSTEM PROMPT ===\n{}", systemPrompt);

                return systemPrompt;
        }

        @Override
        public String buildQuestionCorrectionPrompt(Question question, Course course) {

                var settings = promptSettingsService.getPromptSettings();

                String questionJson = toJson(buildQuestionPayload(question));

                Map<String, String> tokens = new LinkedHashMap<>();
                tokens.put("question_json", questionJson);
                tokens.put("output_format", OUTPUT_FORMAT);

                String prompt = renderTemplate(settings.getCorrectionPromptTemplate(), tokens);

                log.info("=== QUESTION PROMPT ===\n{}", prompt);

                return prompt;
        }

        @Override
        public String buildQuestionCorrectionChatPrompt(
                        Question question,
                        Course course,
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
                tokens.put("output_format", OUTPUT_FORMAT);

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

        private Map<String, Object> buildQuestionPayload(Question question) {
                Map<String, Object> payload = new LinkedHashMap<>();

                payload.put("id", question.getId());
                payload.put("question", question.getQuestion());
                payload.put("question_type", question.getQuestionType());
                payload.put("image", question.getImage());
                payload.put("feedback", question.getFeedback());

                payload.put(
                                "sub_questions",
                                question.getSubQuestions() == null
                                                ? List.of()
                                                : question.getSubQuestions().stream()
                                                                .map(this::buildSubQuestionPayload)
                                                                .toList());

                return payload;
        }

        private Map<String, Object> buildSubQuestionPayload(SubQuestion sq) {
                Map<String, Object> payload = new LinkedHashMap<>();

                payload.put("id", sq.getId());
                payload.put("question", sq.getQuestion());

                payload.put(
                                "answers",
                                sq.getAnswers() == null
                                                ? List.of()
                                                : sq.getAnswers().stream()
                                                                .map(this::buildAnswerPayload)
                                                                .toList());

                return payload;
        }

        private Map<String, Object> buildAnswerPayload(Answer a) {
                Map<String, Object> payload = new LinkedHashMap<>();

                payload.put("id", a.getId());
                payload.put("answer", a.getAnswer());
                payload.put("is_right", a.isRight());

                return payload;
        }
}