package com.quiz.ai.correctionModule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.ai.correctionModule.entity.PromptSettings;

import java.util.Optional;

@Repository
public interface PromptSettingsRepository extends JpaRepository<PromptSettings, String> {
    Optional<PromptSettings> findTopByOrderByUpdatedAtDesc();
}
