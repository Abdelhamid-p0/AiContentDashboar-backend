package com.quiz.ai.repository;

import com.quiz.ai.domains.subject.FinishedCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinishedCourseRepository extends JpaRepository<FinishedCourse, String> {
}
