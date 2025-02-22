package com.rafael.api.service;

import com.rafael.api.client.AIClient;
import com.rafael.api.dto.BookInsightAIResponse;
import com.rafael.api.exception.IntegrationAIException;
import com.rafael.api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
                .switchIfEmpty(Mono.error(new IntegrationAIException("Book not found.", HttpStatus.NOT_FOUND.value())))
                .flatMap(book -> {
                    String prompt = book.getDescription() + " " + book.getTitle() + " by " + book.getAuthor();

                    return aiClient.generateInsight(prompt)
                            .map(aiInsight -> new BookInsightAIResponse(aiInsight.getBook(), aiInsight.getAiInsight()));
                });
    }

}

