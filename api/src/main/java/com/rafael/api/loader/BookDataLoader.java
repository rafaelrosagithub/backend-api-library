package com.rafael.api.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.rafael.api.model.Book;
import com.rafael.api.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class BookDataLoader implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final ObjectMapper objectMapper;

    public BookDataLoader(BookRepository bookRepository, ObjectMapper objectMapper) {
        this.bookRepository = bookRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        InputStream inputStream = TypeReference.class.getResourceAsStream("/books.json");

        List<Book> books = objectMapper.readValue(inputStream, new TypeReference<List<Book>>(){});

        bookRepository.saveAll(books);

        System.out.println("Books were loaded from the JSON file and saved to the database.");
    }

}
