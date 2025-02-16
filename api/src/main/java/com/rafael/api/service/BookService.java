package com.rafael.api.service;

import com.rafael.api.model.Book;
import com.rafael.api.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Optional<Book> updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id)
                .map(existingBook -> {
                    checkAndUpdateNonNullProperties(updatedBook, existingBook);
                    return bookRepository.save(existingBook);
                });
    }

    private void checkAndUpdateNonNullProperties(Book updatedBook, Book existingBook) {
        if (updatedBook.getTitle() != null) existingBook.setTitle(updatedBook.getTitle());
        if (updatedBook.getAuthor() != null) existingBook.setAuthor(updatedBook.getAuthor());
        if (updatedBook.getIsbn() != null) existingBook.setIsbn(updatedBook.getIsbn());
        if (updatedBook.getPublicationYear() != 0) existingBook.setPublicationYear(updatedBook.getPublicationYear());
        if (updatedBook.getDescription() != null) existingBook.setDescription(updatedBook.getDescription());
    }

    public boolean deleteBook(Long id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<Book> searchBooks(String title, String author) {
        if (title != null && author != null) {
            return bookRepository.findByTitleContainingAndAuthorContaining(title, author);
        } else if (title != null) {
            return bookRepository.findByTitleContaining(title);
        } else if (author != null) {
            return bookRepository.findByAuthorContaining(author);
        } else {
            return bookRepository.findAll();
        }
    }

}
