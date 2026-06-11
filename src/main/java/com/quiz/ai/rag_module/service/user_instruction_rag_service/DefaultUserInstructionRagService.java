package com.quiz.ai.rag_module.service.user_instruction_rag_service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.ai.rag_module.entity.pedagogy_documents.PedagogyDocument;
import com.quiz.ai.rag_module.repository.PedagogyDocumentRepository;
import com.quiz.ai.rag_module.service.embedding_service.JinaEmbeddingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultUserInstructionRagService implements UserInstructionRagService {

    private final PedagogyDocumentRepository repository;
    private final JinaEmbeddingService embeddingService;

    @Override
    public String retrieveContext(String instruction) {

        // 1. embedding instruction utilisateur
        List<Double> embedding = embeddingService.generateEmbedding(instruction);

        String vector = embedding.toString();

        // 2. semantic search
        List<PedagogyDocument> docs = repository.searchBySimilarity(vector, 5);

        // 3. build context
        return docs.stream()
                .map(PedagogyDocument::getContent)
                .reduce("", (a, b) -> a + "\n---\n" + b);
    }
}