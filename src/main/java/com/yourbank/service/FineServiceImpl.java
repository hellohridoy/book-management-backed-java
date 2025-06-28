package com.yourbank.service;

import com.yourbank.entity.Fine;
import com.yourbank.entity.Member;
import com.yourbank.exceptions.FineNotFoundException;
import com.yourbank.exceptions.InvalidPaymentException;
import com.yourbank.repository.FineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;

    public FineServiceImpl(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    @Override
    @Transactional
    public Fine createFine(Fine fine) {
        fine.setPaid(false);
        fine.setPaidAmount(BigDecimal.ZERO);
        return fineRepository.save(fine);
    }

    @Override
    public Fine getFineById(Long id) {
        return fineRepository.findById(id)
            .orElseThrow(() -> new FineNotFoundException("Fine not found with id: " + id));
    }

    @Override
    @Transactional
    public Fine updateFine(Fine fine) {
        if (!fineRepository.existsById(fine.getId())) {
            throw new FineNotFoundException("Fine not found with id: " + fine.getId());
        }
        return fineRepository.save(fine);
    }

    @Override
    @Transactional
    public void deleteFine(Long id) {
        if (!fineRepository.existsById(id)) {
            throw new FineNotFoundException("Fine not found with id: " + id);
        }
        fineRepository.deleteById(id);
    }

    @Override
    public List<Fine> getAllFines() {
        return fineRepository.findAll();
    }

    @Override
    public Page<Fine> getAllFines(Pageable pageable) {
        return fineRepository.findAll(pageable);
    }

    @Override
    public List<Fine> getFinesByMember(Member member) {
        return fineRepository.findByMember(member);
    }

    @Override
    public Page<Fine> getFinesByMember(Member member, Pageable pageable) {
        return fineRepository.findByMember(member, pageable);
    }

    @Override
    public List<Fine> getUnpaidFinesByMember(Member member) {
        return fineRepository.findByMemberAndPaidFalse(member);
    }

    @Override
    public BigDecimal getTotalUnpaidFinesByMember(Long memberId) {
        BigDecimal total = fineRepository.sumUnpaidFinesByMember(memberId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public Fine markFineAsPaid(Long fineId, BigDecimal amount, String paymentMethod) {
        Fine fine = getFineById(fineId);

        if (fine.isPaid()) {
            throw new InvalidPaymentException("Fine is already paid");
        }

        if (amount.compareTo(fine.getAmount().subtract(fine.getPaidAmount())) < 0) {
            throw new InvalidPaymentException("Payment amount is less than remaining balance");
        }

        fineRepository.markAsPaid(fineId, amount, LocalDateTime.now(), paymentMethod);

        // Refresh the entity
        fine = getFineById(fineId);
        fine.setPaid(true);
        fine.setPaidDate(LocalDateTime.now());
        fine.setPaymentMethod(paymentMethod);
        fine.setPaidAmount(amount);

        return fine;
    }

    @Override
    @Transactional
    public Fine addPartialPayment(Long fineId, BigDecimal amount) {
        Fine fine = getFineById(fineId);

        if (fine.isPaid()) {
            throw new InvalidPaymentException("Fine is already fully paid");
        }

        BigDecimal newPaidAmount = fine.getPaidAmount().add(amount);
        if (newPaidAmount.compareTo(fine.getAmount()) > 0) {
            throw new InvalidPaymentException("Payment exceeds fine amount");
        }

        fineRepository.addPayment(fineId, amount);

        // Refresh the entity
        fine = getFineById(fineId);
        if (newPaidAmount.compareTo(fine.getAmount()) >= 0) {
            fine.setPaid(true);
            fine.setPaidDate(LocalDateTime.now());
        }

        return fine;
    }

    @Override
    public List<Fine> findFinesByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return fineRepository.findByAmountBetween(minAmount, maxAmount);
    }

    @Override
    public List<Fine> findFinesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return fineRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public Fine getFineByBorrowing(Long borrowingId) {
        return fineRepository.findByBorrowingId(borrowingId);
    }
}
