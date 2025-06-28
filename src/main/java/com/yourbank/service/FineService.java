package com.yourbank.service;

import com.yourbank.entity.Fine;
import com.yourbank.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FineService {
    Fine createFine(Fine fine);
    Fine getFineById(Long id);
    Fine updateFine(Fine fine);
    void deleteFine(Long id);
    List<Fine> getAllFines();
    Page<Fine> getAllFines(Pageable pageable);
    List<Fine> getFinesByMember(Member member);
    Page<Fine> getFinesByMember(Member member, Pageable pageable);
    List<Fine> getUnpaidFinesByMember(Member member);
    BigDecimal getTotalUnpaidFinesByMember(Long memberId);
    Fine markFineAsPaid(Long fineId, BigDecimal amount, String paymentMethod);
    Fine addPartialPayment(Long fineId, BigDecimal amount);
    List<Fine> findFinesByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    List<Fine> findFinesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    Fine getFineByBorrowing(Long borrowingId);
}
