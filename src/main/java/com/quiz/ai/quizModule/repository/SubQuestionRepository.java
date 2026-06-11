package com.quiz.ai.quizModule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.quizModule.entity.question.SubQuestion;

@Repository
public interface SubQuestionRepository extends JpaRepository<SubQuestion, String> {
}
