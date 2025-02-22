package com.rafael.api.dto;

import com.rafael.api.model.Book;

public class BookInsightAIResponse {
    private final Book book;
    private final String aiInsight;

    public BookInsightAIResponse(Book book, String aiInsight) {
        this.book = book;
        this.aiInsight = aiInsight;
    }

    public Book getBook() {
        return book;
    }

    public String getAiInsight() {
        return aiInsight;
    }
}

