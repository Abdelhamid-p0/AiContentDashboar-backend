package com.quiz.ai.infrastructure.llm.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quiz.ai.application.dto.correction.QuestionCorrectionResponse;
import com.quiz.ai.application.dto.question.AnswerResponse;
import com.quiz.ai.application.dto.question.ObjectiveResponse;
import com.quiz.ai.application.dto.question.QuestionResponse;
import com.quiz.ai.application.dto.question.SubQuestionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "quiz-ai.llm.mock-enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class MockLLMClient implements LLMClient {
    private final ObjectMapper objectMapper;

    @Override
    public String chat(String systemMessage, String userMessage) {
        try {
            return objectMapper.writeValueAsString(buildResponse("question-id-placeholder"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to build mock LLM response", e);
        }
    }

    @Override
    public <T> T chatWithStructuredResponse(String systemMessage, String userMessage, Class<T> responseType) {
        try {
            return objectMapper.readValue(chat(systemMessage, userMessage), responseType);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse mock LLM response", e);
        }
    }

    private QuestionCorrectionResponse buildResponse(String questionId) {
        return new QuestionCorrectionResponse(
                List.of("Corrected wording and feedback"),
                "The question was clarified and the answer choices were normalized.",
                "Ambiguous wording in the original question",
                new QuestionResponse(
                        questionId,
                        "What is the solution to 2x - 2 = 2?",
                        "ONE_CHOICE",
                        "question_image",
                        "Corrected feedback",
                        null,
                        "question_audio",
                        1,
                        new ObjectiveResponse("objective-1", "Solve linear equations"),
                        List.of(new SubQuestionResponse(
                                "sub-question-1",
                                "Solve: 2x - 2 = 2",
                                1,
                                List.of(
                                        new AnswerResponse("answer-1", "x = 2", "answer_image_1", true,
                                                "answer_audio_1", 1),
                                        new AnswerResponse("answer-2", "x = 5", "answer_image_2", false,
                                                "answer_audio_2", 2))))));
    }
}