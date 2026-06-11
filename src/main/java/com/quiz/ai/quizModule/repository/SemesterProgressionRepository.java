package com.quiz.ai.quizModule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.quizModule.entity.subject.SemesterProgression;

@Repository
public interface SemesterProgressionRepository extends JpaRepository<SemesterProgression, String> {
}
