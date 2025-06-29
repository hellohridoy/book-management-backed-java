package com.yourbank.service;

import com.yourbank.entity.Borrowing;
import com.yourbank.entity.Fine;
import com.yourbank.entity.User;
import com.yourbank.enums.BorrowingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BorrowingService {
    Borrowing createBorrowing(Borrowing borrowing);
    Borrowing getBorrowingById(Long id);
    Borrowing updateBorrowing(Borrowing borrowing);
    void deleteBorrowing(Long id);
    List<Borrowing> getAllBorrowings();
    Page<Borrowing> getAllBorrowings(Pageable pageable);
    List<Borrowing> getBorrowingsByMember(User user);
    List<Borrowing> getActiveBorrowingsByUser(User user);
    List<Borrowing> getOverdueBorrowings();
    Borrowing returnBook(Long borrowingId, LocalDate returnDate);
    Fine checkForFine(Long borrowingId);
    void processOverdueBorrowings();
    Page<Borrowing> searchBorrowings(Long memberId, Long bookId, BorrowingStatus status, Pageable pageable);
     double calculateOverdueFine(Long id);
}
