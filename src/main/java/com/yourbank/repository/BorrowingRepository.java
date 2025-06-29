package com.yourbank.repository;

import com.yourbank.entity.Borrowing;
import com.yourbank.entity.User;
import com.yourbank.enums.BorrowingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    List<Borrowing> findByUser (User user);
    List<Borrowing> findByBookId(Long bookId);
    List<Borrowing> findByStatus(BorrowingStatus status);

    @Query("SELECT b FROM Borrowing b WHERE b.user = :User  AND b.status = 'ACTIVE'")
    List<Borrowing> findActiveBorrowingsByUser (@Param("User ") User user);

    @Query("SELECT b FROM Borrowing b WHERE b.dueDate < :today AND b.status = 'ACTIVE'")
    List<Borrowing> findOverdueBorrowings(@Param("today") LocalDate today);

    @Query("SELECT b FROM Borrowing b WHERE b.returnDate IS NULL AND b.dueDate < CURRENT_DATE")
    List<Borrowing> findCurrentlyOverdue();

    @Query("SELECT b FROM Borrowing b WHERE b.book.id = :bookId AND b.returnDate IS NULL")
    List<Borrowing> findActiveBorrowingsForBook(@Param("bookId") Long bookId);

    @Modifying
    @Query("UPDATE Borrowing b SET b.status = 'OVERDUE' WHERE b.dueDate < CURRENT_DATE AND b.status = 'ACTIVE'")
    int markOverdueBorrowings();

    Page<Borrowing> findByUser (User  User , Pageable pageable);


    @Query("""
        SELECT b FROM Borrowing b
        WHERE (:userId IS NULL OR b.user.id = :userId)
          AND (:bookId IS NULL OR b.book.id = :bookId)
          AND (:status IS NULL OR b.status = :status)
    """)
    Page<Borrowing> searchBorrowings(
        @Param("userId") Long userId,
        @Param("bookId") Long bookId,
        @Param("status") BorrowingStatus status,
        Pageable pageable);
}
