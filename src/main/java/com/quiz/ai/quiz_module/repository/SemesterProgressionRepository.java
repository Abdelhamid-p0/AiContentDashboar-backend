package com.quiz.ai.quiz_module.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.quiz_module.entity.subject.SemesterProgression;

@Repository
public interface SemesterProgressionRepository extends JpaRepository<SemesterProgression, String> {
}
