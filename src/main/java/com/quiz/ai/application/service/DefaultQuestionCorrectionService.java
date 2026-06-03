package com.quiz.ai.application.service;

import com.quiz.ai.application.dto.correction.QuestionCorrectionResponse;
import com.quiz.ai.application.dto.question.QuestionResponse;
import com.quiz.ai.domains.question.Question;
import com.quiz.ai.infrastructure.llm.client.LLMClient;
import com.quiz.ai.infrastructure.llm.prompt.PromptBuilder;
import com.quiz.ai.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultQuestionCorrectionService implements QuestionCorrectionService {
    private final QuestionRepository questionRepository;
    private final LLMClient llmClient;
    private final PromptBuilder promptBuilder;

    @Override
    public QuestionCorrectionResponse correctQuestion(String questionId) {
        log.info("Starting question correction for questionId: {}", questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));

        var course = question.getQuiz().getCourse();

        String systemMessage = promptBuilder.buildSystemMessage(course);
        String userMessage = promptBuilder.buildQuestionCorrectionPrompt(question, course);

        log.debug("System message: {}", systemMessage);
        log.debug("User message: {}", userMessage);

        try {
            QuestionCorrectionResponse response = llmClient.chatWithStructuredResponse(
                    systemMessage,
                    userMessage,
                    QuestionCorrectionResponse.class);

            log.info("Question correction completed successfully");
            return attachOriginalQuestion(question, response);
        } catch (Exception e) {
            log.error("Error correcting question: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to correct question with LLM", e);
        }
    }

    @Override
    public QuestionCorrectionResponse correctQuestionWithInstruction(
            String questionId,
            String userMessage,
            QuestionCorrectionResponse previousCorrection) {
        log.info("Starting personalized correction for questionId: {}", questionId);

        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException("User instruction is required");
        }

        if (previousCorrection == null) {
            log.warn("Previous correction missing for questionId: {}", questionId);
            return correctQuestion(questionId);
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));

        var course = question.getQuiz().getCourse();

        String systemMessage = promptBuilder.buildSystemMessage(course);
        String prompt = promptBuilder.buildQuestionCorrectionChatPrompt(
                question,
                course,
                previousCorrection,
                userMessage);

        log.debug("System message: {}", systemMessage);
        log.debug("User message: {}", prompt);

        try {
            QuestionCorrectionResponse response = llmClient.chatWithStructuredResponse(
                    systemMessage,
                    prompt,
                    QuestionCorrectionResponse.class);

            log.info("Personalized correction completed successfully");
            return attachOriginalQuestion(question, response);
        } catch (Exception e) {
            log.error("Error correcting question with personalization: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to personalize question correction with LLM", e);
        }
    }

    private QuestionCorrectionResponse attachOriginalQuestion(
            Question question,
            QuestionCorrectionResponse response) {
        QuestionResponse originalQuestion = QuestionResponse.fromEntity(question);

        return new QuestionCorrectionResponse(
                response.corrections(),
                response.explanation(),
                response.detectedErrors(),
                originalQuestion,
                response.improvedQuestion());
    }
}
