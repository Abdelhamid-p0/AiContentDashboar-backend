package com.quiz.ai.ragModule.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.ai.ragModule.service.embedding.PedagogyDocumentEmbeddingService;

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