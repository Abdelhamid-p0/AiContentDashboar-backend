package com.quiz.ai.prompt_builder_module.config.llm.client;

public interface LLMClient {
    /**
     * Send a prompt to the LLM and get a response
     * 
     * @param systemMessage the system message (role and instructions)
     * @param userMessage   the user message (question/content)
     * @return the LLM response
     */
    String chat(String systemMessage, String userMessage);

    /**
     * Send a prompt and get a structured response
     * 
     * @param systemMessage the system message
     * @param userMessage   the user message
     * @param responseType  the type to deserialize into
     * @return the parsed response
     */
    <T> T chatWithStructuredResponse(String systemMessage, String userMessage, Class<T> responseType);
}
