package com.quiz.ai.quizModule.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.quiz.ai.quizModule.dto.question.QuestionResponse;
import com.quiz.ai.quizModule.dto.question.QuestionCorrectionContextResponse;
import com.quiz.ai.quizModule.dto.question.QuestionSummaryResponse;
import com.quiz.ai.quizModule.dto.question.QuestionsResponse;
import com.quiz.ai.quizModule.repository.QuestionRepository;
import com.quiz.ai.quizModule.service.QuestionService;

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

    @Override
    public QuestionCorrectionContextResponse getQuestionCorrectionContext(String questionId) {
        var question = questionRepository.findCorrectionContextById(questionId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Question not found"));
        return QuestionCorrectionContextResponse.fromEntity(question);
    }
}
