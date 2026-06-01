package com.quiz.ai.infrastructure.llm.rag;

import com.quiz.ai.domains.question.Question;

/**
 * Interface for Retrieval-Augmented Generation system
 * Retrieves similar questions and pedagogical context from knowledge base
 * Can be extended with Vector DB, Elasticsearch, or other retrieval systems
 */
public interface RAGContext {
    /**
     * Retrieve similar questions for context
     * 
     * @param question the question to find similar examples for
     * @param limit    maximum number of similar questions to return
     * @return list of similar questions with their corrections
     */
    String retrieveSimilarQuestionsContext(Question question, int limit);

    /**
     * Retrieve pedagogical best practices for the subject/domain
     * 
     * @param subjectId the subject identifier
     * @param domainId  the domain identifier
     * @return pedagogical guidelines as text
     */
    String retrievePedagogicalGuidelines(String subjectId, String domainId);

    /**
     * Retrieve common mistakes and misconceptions
     * 
     * @param questionType the type of question
     * @param level        the student level
     * @return common errors and how to correct them
     */
    String retrieveCommonMistakes(String questionType, String level);
}
