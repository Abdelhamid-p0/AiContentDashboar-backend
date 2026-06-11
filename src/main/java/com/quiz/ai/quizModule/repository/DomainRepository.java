package com.quiz.ai.quizModule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.quizModule.entity.subject.Domain;

@Repository
public interface DomainRepository extends JpaRepository<Domain, String> {
}
