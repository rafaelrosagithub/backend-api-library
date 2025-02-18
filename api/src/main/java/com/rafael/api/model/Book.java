package com.rafael.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title cannot be null")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @NotNull(message = "Author cannot be null")
    @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
    private String author;

    @NotNull(message = "ISBN cannot be null")
    @Pattern(regexp = "^[0-9]{13}$", message = "ISBN must be exactly 13 digits")
    private String isbn;

    @Min(value = 1700, message = "Publication year must be after 1700")
    @Max(value = 2025, message = "Publication year cannot be in the future")
    private int publicationYear;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    public Book() {}

    public Book(String title, String author, String isbn, int publicationYear, String description) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publicationYear=" + publicationYear +
                ", description='" + description + '\'' +
                '}';
    }
}
