package com.quiz.ai.correctionModule.prompt;

import com.quiz.ai.correctionModule.dto.QuestionCorrectionResponse;
import com.quiz.ai.quizModule.dto.course.CourseCorrectionContextResponse;
import com.quiz.ai.quizModule.dto.question.CorrectionQuestionResponse;

public interface PromptBuilder {
    /**
     * Build a system message with role and pedagogical rules
     * 
     * @param course the course context
     * @return the system message
     */
    SystemPromptResult buildSystemMessage(CourseCorrectionContextResponse course);

    /**
     * Build the full user prompt for question correction
     * 
     * @param question the question to correct
     * @param course   the course context
     * @return the user prompt
     */
    String buildQuestionCorrectionPrompt(CorrectionQuestionResponse question, CourseCorrectionContextResponse course);

    /**
     * Build a prompt that refines the correction using user instructions and the
     * previous AI result
     * 
     * @param question           the question to correct
     * @param course             the course context
     * @param previousCorrection the previous AI correction result
     * @param userMessage        the user personalization request
     * @return the user prompt
     */
    String buildQuestionCorrectionChatPrompt(
            CorrectionQuestionResponse question,
            CourseCorrectionContextResponse course,
            QuestionCorrectionResponse previousCorrection,
            String userMessage);
}
