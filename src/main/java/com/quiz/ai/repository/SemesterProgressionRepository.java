package com.quiz.ai.repository;

import com.quiz.ai.domains.subject.SemesterProgression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterProgressionRepository extends JpaRepository<SemesterProgression, String> {
}
