package com.quiz.ai.infrastructure.llm.rag;

import com.quiz.ai.domains.question.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Stub implementation of RAG context
 * Ready to be replaced with real implementation using Vector DB, Elasticsearch,
 * etc.
 */
@Slf4j
@Component
public class StubRAGContext implements RAGContext {

    @Override
    public String retrieveSimilarQuestionsContext(Question question, int limit) {
        log.info("RAG: Retrieving similar questions for question type: {}", question.getQuestionType());
        return """
                [RAG STUB] Similar questions context:
                - This is a stub implementation
                - Replace with real Vector DB implementation for actual similar question retrieval
                - Current question type: """ + question.getQuestionType();
    }

    @Override
    public String retrievePedagogicalGuidelines(String subjectId, String domainId) {
        log.info("RAG: Retrieving pedagogical guidelines for subject: {}, domain: {}", subjectId, domainId);
        return """
                [RAG STUB] Pedagogical guidelines:
                - This is a stub implementation
                - Replace with knowledge base queries for actual guidelines
                - Subject ID: """ + subjectId + ", Domain ID: " + domainId;
    }

    @Override
    public String retrieveCommonMistakes(String questionType, String level) {
        log.info("RAG: Retrieving common mistakes for type: {}, level: {}", questionType, level);
        return """
                [RAG STUB] Common mistakes:
                - This is a stub implementation
                - Replace with knowledge base queries for actual mistakes
                - Question type: """ + questionType + ", Level: " + level;
    }
}
