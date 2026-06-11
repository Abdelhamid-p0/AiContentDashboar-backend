package com.quiz.ai.quizModule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.quizModule.entity.question.Objective;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, String> {
}
