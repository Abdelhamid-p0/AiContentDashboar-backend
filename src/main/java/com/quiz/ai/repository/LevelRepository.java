package com.quiz.ai.repository;

import com.quiz.ai.domains.school.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelRepository extends JpaRepository<Level, String> {
}
