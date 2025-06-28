package com.yourbank.controller;

import com.yourbank.entity.Book;
import com.yourbank.enums.BookStatus;
import com.yourbank.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books/books-infos")
public class BookRestController {

    private final BookRepository bookRepository;

    public BookRestController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Basic CRUD operations
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(savedBook);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        book.setId(id);
        Book updatedBook = bookRepository.save(book);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Search operations
    @GetMapping("/search/title")
    public ResponseEntity<List<Book>> findByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookRepository.findByTitle(title));
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<Book>> findByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookRepository.findByAuthor(author));
    }

    @GetMapping("/search/isbn")
    public ResponseEntity<List<Book>> findByIsbn(@RequestParam String isbn) {
        return ResponseEntity.ok(bookRepository.findByIsbn(isbn));
    }

    @GetMapping("/search/title-author")
    public ResponseEntity<List<Book>> findByTitleAndAuthor(
        @RequestParam String title,
        @RequestParam String author) {
        return ResponseEntity.ok(bookRepository.findByTitleAndAuthor(title, author));
    }

    @GetMapping("/search/title-or-author")
    public ResponseEntity<List<Book>> findByTitleOrAuthor(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String author) {
        return ResponseEntity.ok(bookRepository.findByTitleOrAuthor(title, author));
    }

    // Publisher searches
    @GetMapping("/search/publisher")
    public ResponseEntity<List<Book>> findByPublisher(@RequestParam String publisher) {
        return ResponseEntity.ok(bookRepository.findByPublisher(publisher));
    }

    // Year range search
    @GetMapping("/search/year-range")
    public ResponseEntity<List<Book>> findByBooksPublishedBetween(
        @RequestParam LocalDate startYear,
        @RequestParam LocalDate endYear) {
        return ResponseEntity.ok(bookRepository.findByBooksPublishedBetween(startYear, endYear));
    }

    // Title contains (case insensitive)
    @GetMapping("/search/title-contains")
    public ResponseEntity<List<Book>> findByTitleContainingIgnoreCase(
        @RequestParam String titlePart) {
        return ResponseEntity.ok(bookRepository.findByTitleContainingIgnoreCase(titlePart));
    }

    // Status with pagination
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Book>> findByStatus(
        @PathVariable BookStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title"));
        return ResponseEntity.ok(bookRepository.findByStatus(status, pageable));
    }

    // Available copies operations
    @PostMapping("/{id}/decrement-copies")
    public ResponseEntity<Void> decrementAvailableCopies(
        @PathVariable Long id,
        @RequestParam Integer decrement) {
        bookRepository.decrementAvailableCopies(id, decrement);
        return ResponseEntity.ok().build();
    }

    // Get book with borrowings
    @GetMapping("/{id}/with-borrowings")
    public ResponseEntity<Book> findByIdWithBorrowings(@PathVariable Long id) {
        return ResponseEntity.ok(bookRepository.findByIdWithBorrowings(id));
    }

    // Count by status
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable BookStatus status) {
        return ResponseEntity.ok(bookRepository.countByStatus(status));
    }

    // Most borrowed books
    @GetMapping("/most-borrowed")
    public ResponseEntity<List<Book>> findMostBorrowedBooks() {
        return ResponseEntity.ok(bookRepository.findMostBorrowedBooksJpql());
    }

    // Advanced search with multiple criteria
    @GetMapping("/advanced-search")
    public ResponseEntity<List<Book>> advancedSearch(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) String isbn,
        @RequestParam(required = false) String publisher,
        @RequestParam(required = false) LocalDate startYear,
        @RequestParam(required = false) LocalDate endYear,
        @RequestParam(required = false) BookStatus status) {

        // This is a simplified implementation - you might want to build a more dynamic query
        if (title != null && author != null) {
            return ResponseEntity.ok(bookRepository.findByTitleAndAuthor(title, author));
        } else if (title != null) {
            return ResponseEntity.ok(bookRepository.findByTitle(title));
        } else if (author != null) {
            return ResponseEntity.ok(bookRepository.findByAuthor(author));
        } else if (isbn != null) {
            return ResponseEntity.ok(bookRepository.findByIsbn(isbn));
        } else if (publisher != null) {
            return ResponseEntity.ok(bookRepository.findByPublisher(publisher));
        } else if (startYear != null && endYear != null) {
            return ResponseEntity.ok(bookRepository.findByBooksPublishedBetween(startYear, endYear));
        } else if (status != null) {
            return ResponseEntity.ok(bookRepository.findByStatus(status, Pageable.unpaged()).getContent());
        }

        return ResponseEntity.badRequest().build();
    }
}
