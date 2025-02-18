package com.rafael.api.controller;

import com.rafael.api.dto.BookInsightAIResponse;
import com.rafael.api.model.Book;
import com.rafael.api.service.BookInsightAIService;
import com.rafael.api.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Endpoints related to books")
public class BookController {

    private final BookService bookService;
    private final BookInsightAIService bookInsightService;

    @Autowired
    public BookController(BookService bookService, BookInsightAIService bookInsightService) {
        this.bookService = bookService;
        this.bookInsightService = bookInsightService;
    }

    @PostMapping
    @Operation(summary = "Create a new book")
    @ApiResponse(responseCode = "201", description = "Book created successfully")
    public ResponseEntity<Book> createBook(@RequestBody @Valid Book book) {
        Book savedBook = bookService.saveBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @GetMapping
    @Operation(summary = "Get all books")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the book")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(book);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a book")
    @ApiResponse(responseCode = "202", description = "Successfully updated the book")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody @Valid Book book) {
        return bookService.updateBook(id, book)
                .map(updatedBook -> ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedBook))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book")
    @ApiResponse(responseCode = "204", description = "Successfully deleted the book")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean isDeleted = bookService.deleteBook(id);
        if (isDeleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search books by title or author")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books")
    @ApiResponse(responseCode = "404", description = "No books found")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam(required = false) String title,
                                                  @RequestParam(required = false) String author) {
        List<Book> books = bookService.searchBooks(title, author);
        if (books.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    @GetMapping("/{id}/ai-insights")
    @Operation(summary = "Get AI insights for a book")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved AI insights")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<Mono<BookInsightAIResponse>> getBookInsights(@PathVariable Long id) {
        Mono<BookInsightAIResponse> aiResponse = bookInsightService.getBookInsights(id)
                .onErrorResume(e -> Mono.just(new BookInsightAIResponse(null, "Error generating AI insight: " + e.getMessage())));
        return ResponseEntity.ok(aiResponse);
    }
}
