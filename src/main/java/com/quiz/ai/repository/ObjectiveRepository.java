package com.quiz.ai.repository;

import com.quiz.ai.domains.question.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, String> {
}
