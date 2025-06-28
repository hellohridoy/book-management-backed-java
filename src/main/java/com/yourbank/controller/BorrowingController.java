package com.yourbank.controller;

import com.yourbank.entity.Borrowing;
import com.yourbank.entity.Fine;
import com.yourbank.entity.Member;
import com.yourbank.enums.BorrowingStatus;
import com.yourbank.service.BorrowingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PostMapping
    public ResponseEntity<Borrowing> createBorrowing(@RequestBody Borrowing borrowing) {
        return ResponseEntity.ok(borrowingService.createBorrowing(borrowing));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Borrowing> getBorrowingById(@PathVariable Long id) {
        return ResponseEntity.ok(borrowingService.getBorrowingById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Borrowing> updateBorrowing(@PathVariable Long id, @RequestBody Borrowing borrowing) {
        borrowing.setId(id);
        return ResponseEntity.ok(borrowingService.updateBorrowing(borrowing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable Long id) {
        borrowingService.deleteBorrowing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Borrowing>> getAllBorrowings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "borrowDate,desc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(borrowingService.getAllBorrowings(pageable));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<Borrowing>> getBorrowingsByMember(
        @PathVariable Long memberId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        return ResponseEntity.ok(borrowingService.searchBorrowings(memberId, null, null, pageable));
    }

    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<List<Borrowing>> getActiveBorrowingsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(borrowingService.getActiveBorrowingsByMember(new Member(memberId)));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Borrowing>> getOverdueBorrowings() {
        return ResponseEntity.ok(borrowingService.getOverdueBorrowings());
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<Borrowing> returnBook(
        @PathVariable Long id,
        @RequestParam(required = false) String returnDate) {
        LocalDate parsedDate = returnDate != null ? LocalDate.parse(returnDate) : null;
        return ResponseEntity.ok(borrowingService.returnBook(id, parsedDate));
    }

    @PostMapping("/{id}/check-fine")
    public ResponseEntity<Fine> checkForFine(@PathVariable Long id) {
        Fine fine = borrowingService.checkForFine(id);
        return fine != null ? ResponseEntity.ok(fine) : ResponseEntity.noContent().build();
    }

    @PostMapping("/process-overdue")
    public ResponseEntity<Void> processOverdueBorrowings() {
        borrowingService.processOverdueBorrowings();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Borrowing>> searchBorrowings(
        @RequestParam(required = false) Long memberId,
        @RequestParam(required = false) Long bookId,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());
        return ResponseEntity.ok(borrowingService.searchBorrowings(
            memberId,
            bookId,
            status != null ? BorrowingStatus.valueOf(status) : null,
            pageable
        ));
    }
}
