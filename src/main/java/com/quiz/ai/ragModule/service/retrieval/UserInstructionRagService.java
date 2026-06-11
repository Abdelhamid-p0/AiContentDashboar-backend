package com.quiz.ai.ragModule.service.retrieval;

public interface UserInstructionRagService {

    String retrieveContext(
            String instruction);
}