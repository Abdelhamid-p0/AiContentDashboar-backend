package com.quiz.ai.quizModule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.quizModule.entity.question.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, String> {
}
