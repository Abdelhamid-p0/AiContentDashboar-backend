package com.quiz.ai.quizModule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.quizModule.entity.school.Level;

@Repository
public interface LevelRepository extends JpaRepository<Level, String> {
}
