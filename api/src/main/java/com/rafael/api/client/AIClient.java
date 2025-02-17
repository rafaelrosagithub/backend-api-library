package com.rafael.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AIClient {

    private final WebClient webClient;

    public AIClient(WebClient.Builder webClientBuilder,
                    @Value("${ai.api.url}") String apiUrl,
                    @Value("${ai.api.key}") String apiKey) {
        this.webClient = webClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<String> generateInsight(String prompt) {
        String requestBody = """
        {
          "model": "gpt-4o",
          "messages": [
            { "role": "system", "content": "You are a helpful assistant." },
            { "role": "user", "content": "%s" }
          ]
        }
        """;

        return webClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error generating insight: " + e.getMessage()));
    }
}


