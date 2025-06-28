package com.yourbank.entity;

import com.yourbank.enums.BorrowingStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "borrowings")
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

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
        if (returnDate != null) {
            return false; // Already returned
        }
        return LocalDate.now().isAfter(dueDate);
    }

    public int calculateOverdueDays() {
        if (returnDate != null) {
            // For returned books, calculate based on return date
            return (int) Math.max(0, ChronoUnit.DAYS.between(dueDate, returnDate) - GRACE_PERIOD_DAYS);
        } else if (isOverdue()) {
            // For currently overdue books
            return (int) ChronoUnit.DAYS.between(dueDate, LocalDate.now()) - GRACE_PERIOD_DAYS;
        }
        return 0;
    }

    public BigDecimal calculateFineAmount() {
        int overdueDays = calculateOverdueDays();
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }

        // Cap the maximum fine days
        int daysToCharge = Math.min(overdueDays, MAX_FINE_DAYS);
        return DAILY_FINE_RATE.multiply(new BigDecimal(daysToCharge));
    }

    public boolean shouldApplyFine() {
        return calculateFineAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
        // Auto-set due date (14 days from borrow date by default)
        if (borrowDate != null && this.dueDate == null) {
            this.dueDate = borrowDate.plusDays(14);
        }
    }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        if (returnDate != null) {
            this.status = BorrowingStatus.RETURNED;
        }
    }

    public BorrowingStatus getStatus() { return status; }
    public void setStatus(BorrowingStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Fine getFine() { return fine; }
    public void setFine(Fine fine) { this.fine = fine; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (borrowDate != null && dueDate == null) {
            dueDate = borrowDate.plusDays(14); // Default 2-week loan period
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
