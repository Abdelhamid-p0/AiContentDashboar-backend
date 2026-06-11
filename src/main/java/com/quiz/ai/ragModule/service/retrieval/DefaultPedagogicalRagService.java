package com.quiz.ai.ragModule.service.retrieval;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.ai.ragModule.entity.PedagogyDocument;
import com.quiz.ai.ragModule.repository.PedagogyDocumentRepository;
import com.quiz.ai.ragModule.service.embedding.JinaEmbeddingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultPedagogicalRagService implements PedagogicalRagService {

        private final PedagogyDocumentRepository repository;
        private final JinaEmbeddingService embeddingService;

        @Override
        public String retrieveContext(
                        String level,
                        String subject,
                        String domain,
                        String semester) {

                // 1. Embedding de la requÃªte (filters + context)
                String query = String.join(" ",
                                level, subject, domain, semester);

                List<Double> embedding = embeddingService.generateEmbedding(query);

                // 2. Convert to pgvector format
                String vector = embedding.toString()
                                .replace("[", "[")
                                .replace("]", "]");

                // 3. Similarity search + filters
                List<PedagogyDocument> docs = repository.searchSimilar(
                                vector,
                                level,
                                subject,
                                domain,
                                semester,
                                5);

                // 4. Build context
                return docs.stream()
                                .map(PedagogyDocument::getContent)
                                .reduce("", (a, b) -> a + "\n---\n" + b);
        }
}