package com.quiz.ai.rag_module.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.ai.rag_module.service.embedding_service.PedagogyDocumentEmbeddingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/embeddings")
@RequiredArgsConstructor
public class EmbeddingAdminController {

    private final PedagogyDocumentEmbeddingService service;

    @PostMapping("/generate")
    public String generate() {

        service.generateEmbeddings();

        return "Embeddings generated";
    }
}