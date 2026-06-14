package com.quiz.ai.ragModule.service.retrieval;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.quiz.ai.ragModule.dto.PedagogicalRagContext;
import com.quiz.ai.ragModule.dto.RagRetrievedDocument;
import com.quiz.ai.ragModule.entity.PedagogyDocument;
import com.quiz.ai.ragModule.repository.PedagogyDocumentRepository;
import com.quiz.ai.ragModule.service.embedding.JinaEmbeddingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultPedagogicalRagService implements PedagogicalRagService {

        private final PedagogyDocumentRepository repository;
        private final JinaEmbeddingService embeddingService;

        @Override
        public PedagogicalRagContext retrieveContext(
                        String level,
                        String subject,
                        String domain) {

                String normalizedLevel = normalizeFilter(level);
                String normalizedSubject = normalizeFilter(subject);
                String normalizedDomain = normalizeFilter(domain);

                String query = Stream.of(normalizedLevel, normalizedSubject, normalizedDomain)
                                .filter(value -> value != null && !value.isBlank())
                                .reduce("", (left, right) -> left.isBlank() ? right : left + " " + right);

                List<Double> embedding = embeddingService.generateEmbedding(query);

                String vector = embedding.toString()
                                .replace("[", "[")
                                .replace("]", "]");

                List<PedagogyDocument> docs = repository.searchSimilar(
                                vector,
                                normalizedLevel,
                                normalizedSubject,
                                normalizedDomain,
                                5);

                if (docs.isEmpty()) {
                        log.warn(
                                        "No RAG documents found with filters level={}, subject={}, domain={}. Falling back to semantic search only.",
                                        normalizedLevel,
                                        normalizedSubject,
                                        normalizedDomain);
                        docs = repository.searchBySimilarity(vector, 5);
                }

                log.info("RAG retrieved {} documents for query: {}", docs.size(), query);
                log.info("Retrieved RAG documents: {}", docs.stream()
                                .map(document -> "%s (%s)".formatted(document.getTitle(), document.getDocumentType()))
                                .toList());

                return new PedagogicalRagContext(
                                docs.stream()
                                                .map(RagRetrievedDocument::fromEntity)
                                                .toList());
        }

        private String normalizeFilter(String value) {
                if (value == null || value.isBlank()) {
                        return null;
                }

                return value.trim();
        }
}
