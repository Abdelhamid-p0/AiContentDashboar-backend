package com.quiz.ai.correctionModule.prompt;

import com.quiz.ai.correctionModule.dto.QuestionCorrectionResponse;
import com.quiz.ai.quizModule.entity.question.Question;
import com.quiz.ai.quizModule.entity.subject.Course;

public interface PromptBuilder {
    /**
     * Build a system message with role and pedagogical rules
     * 
     * @param course the course context
     * @return the system message
     */
    String buildSystemMessage(Course course);

    /**
     * Build the full user prompt for question correction
     * 
     * @param question the question to correct
     * @param course   the course context
     * @return the user prompt
     */
    String buildQuestionCorrectionPrompt(Question question, Course course);

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
            Question question,
            Course course,
            QuestionCorrectionResponse previousCorrection,
            String userMessage);
}
