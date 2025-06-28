package com.yourbank.controller;

import com.yourbank.entity.Fine;
import com.yourbank.entity.Member;
import com.yourbank.service.FineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {

    private final FineService fineService;

    public FineController(FineService fineService) {
        this.fineService = fineService;
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

    @GetMapping
    public ResponseEntity<Page<Fine>> getAllFines(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt,desc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return ResponseEntity.ok(fineService.getAllFines(pageable));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<Fine>> getFinesByMember(
        @PathVariable Long memberId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // You'll need to fetch the Member entity first in a real implementation
        return ResponseEntity.ok(fineService.getFinesByMember(new Member(memberId), pageable));
    }

    @GetMapping("/member/{memberId}/unpaid")
    public ResponseEntity<List<Fine>> getUnpaidFinesByMember(@PathVariable Long memberId) {
        // You'll need to fetch the Member entity first in a real implementation
        return ResponseEntity.ok(fineService.getUnpaidFinesByMember(new Member(memberId)));
    }

    @GetMapping("/member/{memberId}/total-unpaid")
    public ResponseEntity<BigDecimal> getTotalUnpaidFinesByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(fineService.getTotalUnpaidFinesByMember(memberId));
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
