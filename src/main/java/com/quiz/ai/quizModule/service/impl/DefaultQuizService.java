package com.quiz.ai.quizModule.service.impl;

import org.springframework.stereotype.Service;

import com.quiz.ai.quizModule.dto.quiz.QuizItemResponse;
import com.quiz.ai.quizModule.dto.quiz.QuizzesByTypeResponse;
import com.quiz.ai.quizModule.entity.quiz.Quiz;
import com.quiz.ai.quizModule.repository.QuizRepository;
import com.quiz.ai.quizModule.service.QuizService;
import com.quiz.ai.quizModule.enums.Type;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DefaultQuizService implements QuizService {
    private final QuizRepository quizRepository;

    public DefaultQuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public QuizzesByTypeResponse getQuizzesByCourseId(String courseId) {
        List<Quiz> quizzes = quizRepository.findByCourseId(courseId);

        List<QuizItemResponse> flashcards = quizzes.stream()
                .filter(q -> q.getType() == Type.FLASHCARD)
                .map(QuizItemResponse::fromEntity)
                .collect(Collectors.toList());

        List<QuizItemResponse> quizList = quizzes.stream()
                .filter(q -> q.getType() == Type.QUIZ)
                .map(QuizItemResponse::fromEntity)
                .collect(Collectors.toList());

        return new QuizzesByTypeResponse(flashcards, quizList);
    }
}
