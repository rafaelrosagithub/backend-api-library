package com.rafael.api.client;

import com.rafael.api.exception.IntegrationAIException;
import com.rafael.api.utils.JsonStringToObjectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class AIClient {

    private static final Logger logger = LoggerFactory.getLogger(AIClient.class);

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
        logger.info("Method: generateInsight, param: {}", prompt);

        Map<String, Object> mapRequestBody = Map.of(
                "model", "gpt-4o-mini",
                "store", true,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );
        String jsonRequestBody = JsonStringToObjectParser.toJson(mapRequestBody );
        return webClient.post()
                .bodyValue(jsonRequestBody)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    String errorMessage = (String) JsonStringToObjectParser.parseJson(errorBody, "error", "message");
                                    return Mono.error(new IntegrationAIException(errorMessage, response.statusCode().value()));
                                })
                )
                .bodyToMono(String.class);
    }
}


