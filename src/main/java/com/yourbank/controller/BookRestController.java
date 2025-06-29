package com.yourbank.controller;

import com.yourbank.dto.AvailableBookSummaryDto;
import com.yourbank.dto.BookDto;
import com.yourbank.entity.Book;
import com.yourbank.enums.BookStatus;
import com.yourbank.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BookRestController {

    private static final Logger logger = LoggerFactory.getLogger(BookRestController.class);


    private final BookRepository bookRepository;

    public BookRestController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Basic CRUD operations
    @PostMapping("/api/v1/books/books-infos")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(savedBook);
    }

    @GetMapping("/api/v1/books/books-infos/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/api/v1/books/books-infos/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        book.setId(id);
        Book updatedBook = bookRepository.save(book);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/api/v1/books/books-infos/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Search operations
    @GetMapping("/api/v1/books/books-infos/search/title")
    public ResponseEntity<List<Book>> findByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookRepository.findByTitle(title));
    }

    @GetMapping("/api/v1/books/books-infos/search/author")
    public ResponseEntity<List<Book>> findByAuthor(@RequestParam String author) {
        return ResponseEntity.ok(bookRepository.findByAuthor(author));
    }

    @GetMapping("/api/v1/books/books-infos/search/isbn")
    public ResponseEntity<List<Book>> findByIsbn(@RequestParam String isbn) {
        return ResponseEntity.ok(bookRepository.findByIsbn(isbn));
    }

    @GetMapping("/api/v1/books/books-infos/search/title-author")
    public ResponseEntity<List<Book>> findByTitleAndAuthor(
        @RequestParam String title,
        @RequestParam String author) {
        return ResponseEntity.ok(bookRepository.findByTitleAndAuthor(title, author));
    }

    @GetMapping("/api/v1/books/books-infos/search/title-or-author")
    public ResponseEntity<List<Book>> findByTitleOrAuthor(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String author) {
        return ResponseEntity.ok(bookRepository.findByTitleOrAuthor(title, author));
    }

    // Publisher searches
    @GetMapping("/api/v1/books/books-infos/search/publisher")
    public ResponseEntity<List<Book>> findByPublisher(@RequestParam String publisher) {
        return ResponseEntity.ok(bookRepository.findByPublisher(publisher));
    }

    // Year range search
    @GetMapping("/api/v1/books/books-infos/search/year-range")
    public ResponseEntity<List<Book>> findByBooksPublishedBetween(
        @RequestParam LocalDate startYear,
        @RequestParam LocalDate endYear) {
        return ResponseEntity.ok(bookRepository.findByBooksPublishedBetween(startYear, endYear));
    }

    // Title contains (case insensitive)
    @GetMapping("/api/v1/books/books-infos/search/title-contains")
    public ResponseEntity<List<Book>> findByTitleContainingIgnoreCase(
        @RequestParam String titlePart) {
        return ResponseEntity.ok(bookRepository.findByTitleContainingIgnoreCase(titlePart));
    }

    // Status with pagination
    @GetMapping("/api/v1/books/books-infos/status/{status}")
    public ResponseEntity<Page<Book>> findByStatus(
        @PathVariable BookStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("title"));
        return ResponseEntity.ok(bookRepository.findByStatus(status, pageable));
    }

    // Available copies operations
    @PostMapping("/api/v1/books/books-infos/{id}/decrement-copies")
    public ResponseEntity<Void> decrementAvailableCopies(
        @PathVariable Long id,
        @RequestParam Integer decrement) {
        bookRepository.decrementAvailableCopies(id, decrement);
        return ResponseEntity.ok().build();
    }

    // Get book with borrowings
    @GetMapping("/api/v1/books/books-infos/{id}/with-borrowings")
    public ResponseEntity<Book> findByIdWithBorrowings(@PathVariable Long id) {
        return ResponseEntity.ok(bookRepository.findByIdWithBorrowings(id));
    }

    // Count by status
    @GetMapping("/api/v1/books/books-infos/count/status/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable BookStatus status) {
        return ResponseEntity.ok(bookRepository.countByStatus(status));
    }

    // Most borrowed books
    @GetMapping("/api/v1/books/books-infos/most-borrowed")
    public ResponseEntity<List<Book>> findMostBorrowedBooks() {
        return ResponseEntity.ok(bookRepository.findMostBorrowedBooksJpql());
    }

    // Advanced search with multiple criteria
    @GetMapping("/api/v1/books/books-infos/advanced-search")
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

    @GetMapping("/api/v1/books/books-infos/available-books")
    public ResponseEntity<List<AvailableBookSummaryDto>> searchBooks(
        @RequestParam(required = false) String searchParams) {

        // Validation: treat empty or blank strings as null (no filter)
        if (searchParams != null && searchParams.trim().isEmpty()) {
            searchParams = null;
        }

        List<Object[]> results = bookRepository.getAvailableBookRaw(searchParams);

        // Map Object[] to DTO
        List<AvailableBookSummaryDto> availableBookSummary = results.stream()
            .map(row -> new AvailableBookSummaryDto(
                (Long) row[0],
                (String) row[1],
                (Integer) row[2],
                BookStatus.valueOf((String) row[3])
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(availableBookSummary);
    }

    @GetMapping("/api/v1/books/books-infos/by-title")
    public ResponseEntity<List<BookDto>> getBooks
        (@RequestParam(required = false) String searchParams) {
        logger.info("Received searchParams: {}", searchParams);
        List<Book> books = (searchParams == null || searchParams.trim().isEmpty())
            ? bookRepository.findAll()
            : bookRepository.findByPublisherContainingIgnoreCaseOrTitleContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            searchParams, searchParams, searchParams
        );

        List<BookDto> dtos = books.stream()
            .map(book -> new BookDto(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublisher(),
                book.getStatus().toString()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


}
