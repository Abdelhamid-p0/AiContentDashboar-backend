package com.quiz.ai.ragModule.service.retrieval;

public interface PedagogicalRagService {

    String retrieveContext(
            String level,
            String subject,
            String domain,
            String semester);
}