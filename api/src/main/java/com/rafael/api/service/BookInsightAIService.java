package com.rafael.api.service;

import com.rafael.api.client.AIClient;
import com.rafael.api.dto.BookInsightAIResponse;
import com.rafael.api.exception.IntegrationAIException;
import com.rafael.api.repository.BookRepository;
import com.rafael.api.utils.JsonStringToObjectParser;
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
                    String prompt = """
                        Generate short and engaging summary:
                        Title: %s
                        Author: %s
                        Description: %s
                        Respond in the following format:
                        Summary:  [generated summary]
                    """.formatted(
                            (book.getTitle() != null && !book.getTitle().isEmpty()) ? book.getTitle() : "No title provided",
                            (book.getAuthor() != null && !book.getAuthor().isEmpty()) ? book.getAuthor() : "No author provided",
                            (book.getDescription() != null && !book.getDescription().isEmpty()) ? book.getDescription() : "No description provided"
                    );
                    return aiClient.generateInsight(prompt)
                            .map(aiInsight ->
                                    new BookInsightAIResponse(book, (String) JsonStringToObjectParser.parseJson(aiInsight, "choices")));
                });
    }

}

