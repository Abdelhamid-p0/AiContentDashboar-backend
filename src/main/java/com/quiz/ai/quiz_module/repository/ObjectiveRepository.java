package com.quiz.ai.quiz_module.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.quiz_module.entity.question.Objective;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, String> {
}
