package com.quiz.ai.prompt_builder_module.config.llm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class LLMClientConfiguration {

    @Bean
    public RestTemplate llmRestTemplate(LLMProperties llmProperties) {
        RestTemplate restTemplate = new RestTemplate(createFactory(llmProperties));
        return restTemplate;
    }

    private ClientHttpRequestFactory createFactory(LLMProperties llmProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        Duration timeout = parseDuration(llmProperties.getTimeout());
        factory.setConnectTimeout((int) timeout.toMillis());
        factory.setReadTimeout((int) timeout.toMillis());
        return factory;
    }

    private Duration parseDuration(String duration) {
        if (duration.endsWith("s")) {
            return Duration.ofSeconds(Long.parseLong(duration.substring(0, duration.length() - 1)));
        } else if (duration.endsWith("m")) {
            return Duration.ofMinutes(Long.parseLong(duration.substring(0, duration.length() - 1)));
        } else {
            return Duration.ofSeconds(30);
        }
    }
}
