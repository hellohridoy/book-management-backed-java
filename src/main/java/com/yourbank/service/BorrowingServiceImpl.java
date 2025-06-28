package com.yourbank.service;

import com.yourbank.entity.*;
import com.yourbank.enums.BorrowingStatus;
import com.yourbank.exceptions.LibraryException;
import com.yourbank.repository.BookRepository;
import com.yourbank.repository.BorrowingRepository;
import com.yourbank.service.BorrowingService;
import com.yourbank.service.FineService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final FineService fineService;

    public BorrowingServiceImpl(BorrowingRepository borrowingRepository,
                                BookRepository bookRepository,
                                FineService fineService) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
        this.fineService = fineService;
    }

    @Override
    @Transactional
    public Borrowing createBorrowing(Borrowing borrowing) {
        // Check book availability
        Book book = bookRepository.findById(borrowing.getBook().getId())
            .orElseThrow(() -> new LibraryException.BookNotAvailableException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new LibraryException.BookNotAvailableException("No available copies of this book");
        }

        // Set default dates if not provided
        if (borrowing.getBorrowDate() == null) {
            borrowing.setBorrowDate(LocalDate.now());
        }

        // Decrement available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return borrowingRepository.save(borrowing);
    }

    @Override
    public Borrowing getBorrowingById(Long id) {
        return borrowingRepository.findById(id)
            .orElseThrow(() -> new LibraryException.BorrowingNotFoundException("Borrowing not found with id: " + id));
    }

    @Override
    @Transactional
    public Borrowing updateBorrowing(Borrowing borrowing) {
        if (!borrowingRepository.existsById(borrowing.getId())) {
            throw new LibraryException.BorrowingNotFoundException("Borrowing not found with id: " + borrowing.getId());
        }
        return borrowingRepository.save(borrowing);
    }

    @Override
    @Transactional
    public void deleteBorrowing(Long id) {
        Borrowing borrowing = getBorrowingById(id);

        // If book wasn't returned, increment available copies
        if (borrowing.getReturnDate() == null) {
            Book book = borrowing.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        }

        borrowingRepository.deleteById(id);
    }

    @Override
    public List<Borrowing> getAllBorrowings() {
        return borrowingRepository.findAll();
    }

    @Override
    public Page<Borrowing> getAllBorrowings(Pageable pageable) {
        return borrowingRepository.findAll(pageable);
    }

    @Override
    public List<Borrowing> getBorrowingsByMember(Member member) {
        return borrowingRepository.findByMember(member);
    }

    @Override
    public List<Borrowing> getActiveBorrowingsByMember(Member member) {
        return borrowingRepository.findActiveBorrowingsByMember(member);
    }

    @Override
    public List<Borrowing> getOverdueBorrowings() {
        return borrowingRepository.findCurrentlyOverdue();
    }

    @Override
    @Transactional
    public Borrowing returnBook(Long borrowingId, LocalDate returnDate) {
        Borrowing borrowing = getBorrowingById(borrowingId);

        if (borrowing.getReturnDate() != null) {
            throw new IllegalStateException("Book already returned");
        }

        // Update borrowing record
        borrowing.setReturnDate(returnDate != null ? returnDate : LocalDate.now());
        borrowing.setStatus(BorrowingStatus.RETURNED);

        // Increment available copies
        Book book = borrowing.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return borrowingRepository.save(borrowing);
    }

    @Override
    @Transactional
    public Fine checkForFine(Long borrowingId) {
        Borrowing borrowing = getBorrowingById(borrowingId);

        if (borrowing.getReturnDate() == null) {
            throw new IllegalStateException("Book must be returned first");
        }

        if (borrowing.getFine() != null) {
            return borrowing.getFine(); // Fine already exists
        }

        BigDecimal fineAmount = borrowing.calculateFineAmount();
        if (fineAmount.compareTo(BigDecimal.ZERO) > 0) {
            Fine fine = new Fine();
            fine.setBorrowing(borrowing);
            fine.setMember(borrowing.getMember());
            fine.setAmount(fineAmount);
            fine.setOverdueDays(borrowing.calculateOverdueDays());
            fine.setPaid(false);

            Fine savedFine = fineService.createFine(fine);
            borrowing.setFine(savedFine);
            borrowingRepository.save(borrowing);

            return savedFine;
        }

        return null; // No fine applicable
    }

    @Override
    @Transactional
    public void processOverdueBorrowings() {
        borrowingRepository.markOverdueBorrowings();
    }

    @Override
    public Page<Borrowing> searchBorrowings(Long memberId, Long bookId, BorrowingStatus status, Pageable pageable) {
        return borrowingRepository.searchBorrowings(memberId, bookId, status, pageable);
    }
}
