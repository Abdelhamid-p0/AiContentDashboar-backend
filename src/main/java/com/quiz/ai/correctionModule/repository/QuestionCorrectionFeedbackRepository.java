package com.quiz.ai.correctionModule.repository;

import com.quiz.ai.correctionModule.entity.QuestionCorrectionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCorrectionFeedbackRepository extends JpaRepository<QuestionCorrectionFeedback, String> {
}