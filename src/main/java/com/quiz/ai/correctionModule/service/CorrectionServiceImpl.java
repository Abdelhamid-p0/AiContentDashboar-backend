package com.quiz.ai.correctionModule.service;

import com.quiz.ai.correctionModule.prompt.PromptBuilder;
import com.quiz.ai.correctionModule.dto.QuestionCorrectionResponse;
import com.quiz.ai.correctionModule.dto.RagContextDocumentResponse;
import com.quiz.ai.quizModule.dto.question.CorrectionQuestionResponse;
import com.quiz.ai.quizModule.service.QuestionService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorrectionServiceImpl implements CorrectionService {
    private final QuestionService questionService;
    private final ChatClient chatClient;
    private final PromptBuilder promptBuilder;

    @Override
    public QuestionCorrectionResponse correctQuestion(String questionId) {
        log.info("Starting question correction for questionId: {}", questionId);

        var correctionContext = questionService.getQuestionCorrectionContext(questionId);
        var question = correctionContext.question();
        var course = correctionContext.course();

        var systemPrompt = promptBuilder.buildSystemMessage(course);
        String userMessage = promptBuilder.buildQuestionCorrectionPrompt(question, course);

        log.debug("System message: {}", systemPrompt.message());
        log.debug("User message: {}", userMessage);

        try {
            QuestionCorrectionResponse response = chatClient.prompt()
                    .system(systemPrompt.message())
                    .user(userMessage)
                    .call()
                    .entity(QuestionCorrectionResponse.class);

            log.info("Question correction completed successfully");
            return attachOriginalQuestion(question, response, systemPrompt.ragContext());
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

        var correctionContext = questionService.getQuestionCorrectionContext(questionId);
        var question = correctionContext.question();
        var course = correctionContext.course();

        var systemPrompt = promptBuilder.buildSystemMessage(course);
        String prompt = promptBuilder.buildQuestionCorrectionChatPrompt(
                question,
                course,
                previousCorrection,
                userMessage);

        log.debug("System message: {}", systemPrompt.message());
        log.debug("User message: {}", prompt);

        try {
            QuestionCorrectionResponse response = chatClient.prompt()
                    .system(systemPrompt.message())
                    .user(prompt)
                    .call()
                    .entity(QuestionCorrectionResponse.class);

            log.info("Personalized correction completed successfully");
            return attachOriginalQuestion(question, response, systemPrompt.ragContext());
        } catch (Exception e) {
            log.error("Error correcting question with personalization: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to personalize question correction with LLM", e);
        }
    }

    private QuestionCorrectionResponse attachOriginalQuestion(
            CorrectionQuestionResponse question,
            QuestionCorrectionResponse response,
            List<RagContextDocumentResponse> ragContext) {
        return new QuestionCorrectionResponse(
                response.corrections(),
                response.explanation(),
                response.detectedErrors(),
                question,
                ragContext,
                response.improvedQuestion());
    }
}
