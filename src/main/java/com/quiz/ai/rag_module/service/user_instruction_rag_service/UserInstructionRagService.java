package com.quiz.ai.rag_module.service.user_instruction_rag_service;

public interface UserInstructionRagService {

    String retrieveContext(
            String instruction);
}