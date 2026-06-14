package com.quiz.ai.ragModule.service.retrieval;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.ai.ragModule.entity.PedagogyDocument;
import com.quiz.ai.ragModule.repository.PedagogyDocumentRepository;
import com.quiz.ai.ragModule.service.embedding.JinaEmbeddingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Generated embedding for instruction: {}", vector);

        // 2. semantic search
        List<PedagogyDocument> docs = repository.searchBySimilarity(vector, 5);

        log.info("RAG retrieved {} documents for instruction: {}", docs.size(), instruction);
        log.info("Retrieved documents contents: {}", docs.stream()
                .map(PedagogyDocument::getContent)
                .toList());

        // 3. build context
        return docs.stream()
                .map(PedagogyDocument::getContent)
                .reduce("", (a, b) -> a + "\n---\n" + b);
    }
}