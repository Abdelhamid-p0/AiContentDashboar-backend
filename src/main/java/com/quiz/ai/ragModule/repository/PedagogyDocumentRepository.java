package com.quiz.ai.ragModule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quiz.ai.ragModule.entity.PedagogyDocument;

public interface PedagogyDocumentRepository
                extends JpaRepository<PedagogyDocument, String> {

        @Query(value = """
                            SELECT *
                            FROM pedagogy_documents
                            WHERE (:level IS NULL OR level = :level)
                              AND (:subject IS NULL OR subject = :subject)
                              AND (:domain IS NULL OR domain = :domain)
                            ORDER BY embedding <-> CAST(:embedding AS vector)
                            LIMIT :limit
                        """, nativeQuery = true)
        List<PedagogyDocument> searchSimilar(
                        @Param("embedding") String embedding,
                        @Param("level") String level,
                        @Param("subject") String subject,
                        @Param("domain") String domain,
                        @Param("limit") int limit);

        @Query(value = """
                            SELECT *
                            FROM pedagogy_documents
                            ORDER BY embedding <-> CAST(:embedding AS vector)
                            LIMIT :limit
                        """, nativeQuery = true)
        List<PedagogyDocument> searchBySimilarity(
                        @Param("embedding") String embedding,
                        @Param("limit") int limit);

        @Query(value = """
                        UPDATE pedagogy_documents
                        SET embedding = CAST(:embedding AS vector)
                        WHERE id = :id
                        """, nativeQuery = true)
        @org.springframework.data.jpa.repository.Modifying
        void updateEmbedding(
                        @Param("id") String id,
                        @Param("embedding") String embedding);
}