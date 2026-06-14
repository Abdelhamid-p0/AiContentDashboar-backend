package com.quiz.ai.ragModule.service.retrieval;

import com.quiz.ai.ragModule.dto.PedagogicalRagContext;

public interface PedagogicalRagService {

    PedagogicalRagContext retrieveContext(
            String level,
            String subject,
            String domain);
}
