package com.quiz.ai.application.service;

import com.quiz.ai.application.dto.question.QuestionResponse;
import com.quiz.ai.application.dto.question.QuestionSummaryResponse;
import com.quiz.ai.application.dto.question.QuestionsResponse;
import com.quiz.ai.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DefaultQuestionService implements QuestionService {
    private final QuestionRepository questionRepository;

    @Override
    public QuestionsResponse getQuestionsByQuizId(String quizId) {
        var questions = questionRepository.findSummariesByQuizId(quizId);
        var questionResponses = questions.stream()
                .map(QuestionSummaryResponse::fromEntity)
                .toList();
        return new QuestionsResponse(questionResponses);
    }

    @Override
    public QuestionResponse getQuestionById(String questionId) {
        var question = questionRepository.findDetailedById(questionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Question not found"));
        return QuestionResponse.fromEntity(question);
    }
}
