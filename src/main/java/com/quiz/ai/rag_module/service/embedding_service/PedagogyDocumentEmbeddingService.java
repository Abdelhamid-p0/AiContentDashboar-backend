package com.quiz.ai.rag_module.service.embedding_service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quiz.ai.rag_module.entity.pedagogy_documents.PedagogyDocument;
import com.quiz.ai.rag_module.repository.PedagogyDocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedagogyDocumentEmbeddingService {

    private final PedagogyDocumentRepository repository;
    private final JinaEmbeddingService jinaEmbeddingService;

    public void generateEmbeddings() {

        List<PedagogyDocument> documents = repository.findAll();

        for (PedagogyDocument doc : documents) {

            List<Double> embedding = jinaEmbeddingService.generateEmbedding(
                    doc.getContent());

            float[] embeddingArray = new float[embedding.size()];

            for (int i = 0; i < embedding.size(); i++) {
                embeddingArray[i] = embedding.get(i).floatValue();
            }

            doc.setEmbedding(embeddingArray);

            repository.save(doc);
        }
    }
}