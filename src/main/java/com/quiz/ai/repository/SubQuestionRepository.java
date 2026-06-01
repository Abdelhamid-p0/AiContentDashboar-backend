package com.quiz.ai.repository;

import com.quiz.ai.domains.question.SubQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubQuestionRepository extends JpaRepository<SubQuestion, String> {
}
