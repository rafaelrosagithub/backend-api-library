package com.rafael.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafael.api.service.BookInsightAIService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rafael.api.model.Book;
import com.rafael.api.service.BookService;
import com.rafael.api.exception.IntegrationAIException;
import com.rafael.api.dto.BookInsightAIResponse;

import reactor.core.publisher.Mono;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class BookControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private BookInsightAIService bookInsightService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldCreateBookSuccessfully() throws Exception {
        Book book = new Book(
                "Spring Boot Testing",
                "John Doe",
                "9781234567897",
                2024,
                "A guide to testing in Spring Boot"
        );

        when(bookService.saveBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Spring Boot Testing"))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(jsonPath("$.isbn").value("9781234567897"))
                .andExpect(jsonPath("$.publicationYear").value(2024))
                .andExpect(jsonPath("$.description").value("A guide to testing in Spring Boot"));
    }


    @Test
    void shouldReturnBookById() throws Exception {
        Book book = new Book(
                "Clean Code",
                "Robert C. Martin",
                "0987654321",
                2008,
                "A book about writing clean code.");

        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"));
    }


    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        when(bookService.getBookById(99L)).thenReturn(null);

        mockMvc.perform(get("/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateBookSuccessfully() throws Exception {
        Book updatedBook = new Book(
                "Refactoring",
                "Martin Fowler",
                "1122334455667",
                2019,
                "A book about improving code structure.");

        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(Optional.of(updatedBook));

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBook)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.title").value("Refactoring"))
                .andExpect(jsonPath("$.author").value("Martin Fowler"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonexistentBook() throws Exception {
        Book bookToUpdate = new Book(
                "Dummy Title",
                "Dummy Author",
                "1122334455667",
                2020,
                "Dummy description");

        when(bookService.updateBook(eq(99L), any(Book.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookToUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteBookSuccessfully() throws Exception {
        when(bookService.deleteBook(1L)).thenReturn(true);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonexistentBook() throws Exception {
        when(bookService.deleteBook(99L)).thenReturn(false);

        mockMvc.perform(delete("/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnListOfBooks() throws Exception {
        List<Book> books = Arrays.asList(
                new Book("Book 1", "Author 1", "1234567890123", 2020, "Description 1"),
                new Book("Book 2", "Author 2", "0987654321132", 2021, "Description 2")
        );

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldSearchBooksByTitle() throws Exception {
        List<Book> books = Collections.singletonList(
                new Book("Spring Boot", "Craig Walls", "1234567890123", 2019, "A book about Spring Boot.")
        );

        when(bookService.searchBooks("Spring Boot", null)).thenReturn(books);

        mockMvc.perform(get("/books/search?title=Spring Boot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Spring Boot"));
    }

    @Test
    void shouldReturn404WhenSearchHasNoResults() throws Exception {
        when(bookService.searchBooks("Unknown", null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/books/search?title=Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnAIInsightForBook() throws Exception {
        BookInsightAIResponse aiResponse = new BookInsightAIResponse(new Book(), "AI-generated insight");
        when(bookInsightService.getBookInsights(1L)).thenReturn(Mono.just(aiResponse));

        mockMvc.perform(get("/books/1/ai-insights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.insight").value("AI-generated insight"));
    }

    @Test
    void shouldHandleAIIntegrationError() throws Exception {
        when(bookInsightService.getBookInsights(1L))
                .thenReturn(Mono.error(new IntegrationAIException("AI service is down.")));

        mockMvc.perform(get("/books/1/ai-insights"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("AI service is down.")));
    }
}
