package com.quiz.ai.rag_module.service.pedagogical_rag_service;

public interface PedagogicalRagService {

    String retrieveContext(
            String level,
            String subject,
            String domain,
            String semester);
}