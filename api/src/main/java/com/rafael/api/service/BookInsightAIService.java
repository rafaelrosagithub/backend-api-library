package com.rafael.api.service;

import com.rafael.api.client.AIClient;
import com.rafael.api.dto.BookInsightAIResponse;
import com.rafael.api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BookInsightAIService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AIClient aiClient;

    public Mono<BookInsightAIResponse> getBookInsights(Long id) {
        return Mono.justOrEmpty(bookRepository.findById(id))
                .switchIfEmpty(Mono.error(new RuntimeException("Book not found.")))
                .flatMap(book -> {
                    String prompt = book.getDescription() + " " + book.getTitle() + " by " + book.getAuthor();

                    return aiClient.generateInsight(prompt)
                            .map(aiInsight -> new BookInsightAIResponse(book, aiInsight));
                })
                .onErrorResume(e -> Mono.just(new BookInsightAIResponse(null, "Error generating AI insight: " + e.getMessage())));
    }





}

