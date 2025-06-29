package com.yourbank.controller;

import com.yourbank.entity.Fine;
import com.yourbank.entity.User;
import com.yourbank.repository.UserRepository;
import com.yourbank.service.FineService;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    private final UserRepository userRepository;

    public FineController(FineService fineService, UserRepository userRepository) {
        this.fineService = fineService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Fine> createFine(@RequestBody Fine fine) {
        return ResponseEntity.ok(fineService.createFine(fine));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fine> getFineById(@PathVariable Long id) {
        return ResponseEntity.ok(fineService.getFineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fine> updateFine(@PathVariable Long id, @RequestBody Fine fine) {
        fine.setId(id);
        return ResponseEntity.ok(fineService.updateFine(fine));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFine(@PathVariable Long id) {
        fineService.deleteFine(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/fines-infos")
    public ResponseEntity<Page<Fine>> getAllFines(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(fineService.getAllFines(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Fine>> getFinesByUser(
        @PathVariable Long id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(fineService.getFinesByUser(new User(id), pageable));
    }

    @GetMapping("/user/{userId}/unpaid")
    public ResponseEntity<List<Fine>> getUnpaidFinesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(fineService.getUnpaidFinesByUser(new User(userId)));
    }

    @GetMapping("/user/{userId}/total-unpaid")
    public ResponseEntity<BigDecimal> getTotalUnpaidFinesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(fineService.getTotalUnpaidFinesByUser(userId));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Fine> payFine(
        @PathVariable Long id,
        @RequestParam BigDecimal amount,
        @RequestParam String paymentMethod) {
        return ResponseEntity.ok(fineService.markFineAsPaid(id, amount, paymentMethod));
    }

    @PostMapping("/{id}/partial-pay")
    public ResponseEntity<Fine> partialPayFine(
        @PathVariable Long id,
        @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(fineService.addPartialPayment(id, amount));
    }

    @GetMapping("/amount-range")
    public ResponseEntity<List<Fine>> findFinesByAmountRange(
        @RequestParam BigDecimal min,
        @RequestParam BigDecimal max) {
        return ResponseEntity.ok(fineService.findFinesByAmountRange(min, max));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Fine>> findFinesByDateRange(
        @RequestParam String start,
        @RequestParam String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return ResponseEntity.ok(fineService.findFinesByDateRange(startDate, endDate));
    }

    @GetMapping("/borrowing/{borrowingId}")
    public ResponseEntity<Fine> getFineByBorrowing(@PathVariable Long borrowingId) {
        return ResponseEntity.ok(fineService.getFineByBorrowing(borrowingId));
    }
}
