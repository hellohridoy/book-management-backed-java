package com.yourbank.repository;

import com.yourbank.entity.Fine;
import com.yourbank.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    // Find fines by user
    List<Fine> findByUser(User user);

    // Find fines by user with pagination
    Page<Fine> findByUser(User user, Pageable pageable);

    // Find unpaid fines by user
    List<Fine> findByUserAndPaidFalse(User user);

    // Find paid fines
    List<Fine> findByPaidTrue();

    // Find unpaid fines
    List<Fine> findByPaidFalse();

    // Find fines within amount range
    List<Fine> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    // Find fines issued between dates
    List<Fine> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find fines by overdue days greater than
    List<Fine> findByOverdueDaysGreaterThan(Integer days);

    // Find fine for a specific borrowing
    Fine findByBorrowingId(Long borrowingId);

    // Calculate total unpaid fines amount for a user
    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.user.id = :userId AND f.paid = false")
    BigDecimal sumUnpaidFinesByUser(@Param("userId") Long userId);

    // Mark fine as paid
    @Modifying
    @Query("UPDATE Fine f SET f.paid = true, f.paidDate = :paidDate, " +
        "f.paymentMethod = :paymentMethod, f.paidAmount = :amount " +
        "WHERE f.id = :fineId")
    int markAsPaid(@Param("fineId") Long fineId,
                   @Param("amount") BigDecimal amount,
                   @Param("paidDate") LocalDateTime paidDate,
                   @Param("paymentMethod") String paymentMethod);

    // Partial payment
    @Modifying
    @Query("UPDATE Fine f SET f.paidAmount = f.paidAmount + :amount " +
        "WHERE f.id = :fineId")
    int addPayment(@Param("fineId") Long fineId,
                   @Param("amount") BigDecimal amount);
}
