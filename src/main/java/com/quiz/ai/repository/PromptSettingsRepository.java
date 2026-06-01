package com.quiz.ai.repository;

import com.quiz.ai.domains.prompt.PromptSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromptSettingsRepository extends JpaRepository<PromptSettings, String> {
    Optional<PromptSettings> findTopByOrderByUpdatedAtDesc();
}
