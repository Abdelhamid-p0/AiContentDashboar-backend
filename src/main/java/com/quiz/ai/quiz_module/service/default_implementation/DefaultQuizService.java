package com.quiz.ai.quiz_module.service.default_implementation;

import org.springframework.stereotype.Service;

import com.quiz.ai.quiz_module.dto.quiz.QuizItemResponse;
import com.quiz.ai.quiz_module.dto.quiz.QuizzesByTypeResponse;
import com.quiz.ai.quiz_module.entity.quiz.Quiz;
import com.quiz.ai.quiz_module.repository.QuizRepository;
import com.quiz.ai.quiz_module.service.interfaces.QuizService;
import com.quiz.ai.quiz_module.enums.Type;

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
