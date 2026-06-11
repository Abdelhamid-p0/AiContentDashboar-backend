package com.quiz.ai.rag_module.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quiz.ai.rag_module.entity.pedagogy_documents.PedagogyDocument;

public interface PedagogyDocumentRepository
    extends JpaRepository<PedagogyDocument, String> {

  @Query(value = """
          SELECT *
          FROM pedagogy_documents
          WHERE (:level IS NULL OR level = :level)
            AND (:subject IS NULL OR subject = :subject)
            AND (:domain IS NULL OR domain = :domain)
            AND (:semester IS NULL OR semester = :semester)
          ORDER BY embedding <-> CAST(:embedding AS vector)
          LIMIT :limit
      """, nativeQuery = true)
  List<PedagogyDocument> searchSimilar(
      @Param("embedding") String embedding,
      @Param("level") String level,
      @Param("subject") String subject,
      @Param("domain") String domain,
      @Param("semester") String semester,
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
}