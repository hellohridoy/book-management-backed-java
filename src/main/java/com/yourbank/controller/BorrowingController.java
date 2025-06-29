package com.yourbank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yourbank.dto.BorrowingRequestDto;
import com.yourbank.entity.Book;
import com.yourbank.entity.Borrowing;
import com.yourbank.entity.Fine;
import com.yourbank.entity.User;
import com.yourbank.enums.BorrowingStatus;
import com.yourbank.repository.BookRepository;
import com.yourbank.repository.UserRepository;
import com.yourbank.service.BorrowingService;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@RestController
public class BorrowingController {

    private final BorrowingService borrowingService;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BorrowingController(BorrowingService borrowingService,
                               BookRepository bookRepository,
                               UserRepository userRepository) {
        this.borrowingService = borrowingService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/api/borrowings")
    public ResponseEntity<?> createBorrowing(@RequestBody BorrowingRequestDto dto) {
        Optional<User> optionalUser = userRepository.findById(dto.getUserId());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("User not found with ID: " + dto.getUserId());
        }

        Optional<Book> optionalBook = bookRepository.findById(dto.getBookId());
        if (optionalBook.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Book not found with ID: " + dto.getBookId());
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(optionalUser.get());
        borrowing.setBook(optionalBook.get());
        borrowing.setBorrowDate(dto.getBorrowDate());

        Borrowing savedBorrowing = borrowingService.createBorrowing(borrowing);
        return ResponseEntity.ok(savedBorrowing);
    }

    @GetMapping("/api/borrowings/{id}")
    public ResponseEntity<Borrowing> getBorrowingById(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.getBorrowingById(id));
    }

    @PutMapping(value = "/api/borrowings/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Borrowing> updateBorrowing(
        @PathVariable Long id,
        @RequestPart("borrowing") String borrowingJson,
        @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Borrowing borrowing = objectMapper.readValue(borrowingJson, Borrowing.class);
        borrowing.setId(id);

        return ResponseEntity.ok(borrowingService.updateBorrowing(borrowing));
    }

    @DeleteMapping("/api/borrowings/{id}")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable Long id) {
        borrowingService.deleteBorrowing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/borrowings/borrows-infos")
    public ResponseEntity<Page<Borrowing>> getAllBorrowings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "borrowDate,desc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(borrowingService.getAllBorrowings(pageable));
    }

    @GetMapping("/api/borrowings/user/{userId}")
    public ResponseEntity<Page<Borrowing>> getBorrowingsByUser(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        return ResponseEntity.ok(borrowingService.searchBorrowings(userId, null, null, pageable));
    }

    @GetMapping("/api/borrowings/user/{userId}/active")
    public ResponseEntity<List<Borrowing>> getActiveBorrowingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowingService.getActiveBorrowingsByUser(new User(userId)));
    }

    @GetMapping("/api/borrowings/overdue")
    public ResponseEntity<List<Borrowing>> getOverdueBorrowings() {
        return ResponseEntity.ok(borrowingService.getOverdueBorrowings());
    }

    @PostMapping("/api/borrowings/{id}/return")
    public ResponseEntity<Borrowing> returnBook(
        @PathVariable Long id,
        @RequestParam(required = false) String returnDate) {
        LocalDate parsedDate = returnDate != null ? LocalDate.parse(returnDate) : null;
        return ResponseEntity.ok(borrowingService.returnBook(id, parsedDate));
    }

    @PutMapping("/api/borrowings/{id}/return")
    public ResponseEntity<Borrowing> returnBookJson(
        @PathVariable Long id,
        @RequestBody Map<String, String> payload) {
        LocalDate returnDate = LocalDate.parse(payload.get("returnDate"));
        return ResponseEntity.ok(borrowingService.returnBook(id, returnDate));
    }

    @PostMapping("/api/borrowings/{id}/check-fine")
    public ResponseEntity<Fine> checkForFine(@PathVariable Long id) {
        Fine fine = borrowingService.checkForFine(id);
        return fine != null ? ResponseEntity.ok(fine) : ResponseEntity.noContent().build();
    }

    @PostMapping("/api/borrowings/process-overdue")
    public ResponseEntity<Void> processOverdueBorrowings() {
        borrowingService.processOverdueBorrowings();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/borrowings/search")
    public ResponseEntity<Page<Borrowing>> searchBorrowings(
        @RequestParam(required = false) Long userId,
        @RequestParam(required = false) Long bookId,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        return ResponseEntity.ok(borrowingService.searchBorrowings(
            userId,
            bookId,
            status != null ? BorrowingStatus.valueOf(status) : null,
            pageable
        ));
    }

    @GetMapping("/api/borrowings/{id}/fine")
    public ResponseEntity<Double> calculateFine(@PathVariable Long id) {
        double fine = borrowingService.calculateOverdueFine(id);
        return ResponseEntity.ok(fine);
    }
}
