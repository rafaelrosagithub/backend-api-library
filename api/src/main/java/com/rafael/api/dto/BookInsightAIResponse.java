package com.rafael.api.dto;

import com.rafael.api.model.Book;

public class BookInsightAIResponse {
    private Book book;
    private String aiInsight;

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

