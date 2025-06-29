package com.yourbank.entity;

import com.yourbank.enums.BorrowingStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@Table(name = "borrowings")
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Replacing Member

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDate borrowDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowingStatus status = BorrowingStatus.ACTIVE;

    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "borrowing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Fine fine;

    // Constants for fine calculation
    public static final BigDecimal DAILY_FINE_RATE = new BigDecimal("1.00");
    public static final int GRACE_PERIOD_DAYS = 2;
    public static final int MAX_FINE_DAYS = 30;

    // Constructors
    public Borrowing() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Business logic methods
    public boolean isOverdue() {
        if (returnDate != null) return false;
        return LocalDate.now().isAfter(dueDate);
    }

    public int calculateOverdueDays() {
        if (returnDate != null) {
            return (int) Math.max(0, ChronoUnit.DAYS.between(dueDate, returnDate) - GRACE_PERIOD_DAYS);
        } else if (isOverdue()) {
            return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now()) - GRACE_PERIOD_DAYS;
        }
        return 0;
    }

    public BigDecimal calculateFineAmount() {
        int overdueDays = calculateOverdueDays();
        if (overdueDays <= 0) return BigDecimal.ZERO;

        int daysToCharge = Math.min(overdueDays, MAX_FINE_DAYS);
        return DAILY_FINE_RATE.multiply(new BigDecimal(daysToCharge));
    }

    public boolean shouldApplyFine() {
        return calculateFineAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (borrowDate != null && dueDate == null) {
            dueDate = borrowDate.plusDays(14);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
