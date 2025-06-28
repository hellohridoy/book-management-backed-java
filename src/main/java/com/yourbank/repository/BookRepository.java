package com.yourbank.repository;

import com.yourbank.entity.Book;
import com.yourbank.enums.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

//    @Query("SELECT b FROM Book b WHERE b.status = com.yourbank.enums.BookStatus.AVAILABLE")
//    List<Book> findAvailableBooks();
//
//    @Query("SELECT b from Book b where b.isbn = :isbn")
//    Book findBookByIsbn(@Param("isbn") String isbn);
//
//    @Query("select br from Borrowing br where br.member.email =:email")
//    List<Borrowing> findBorrowingByEmail(@Param("email") String email);
//
//
//    @Query("select count (b) from Book b where b.status = com.yourbank.enums.BookStatus.BORROWED")
//    int countBorrowingStatus();
//
//    @Query("SELECT b FROM Book b WHERE b.availableCopies < 3")
//    List<Book> findBooksWithLowAvailability();
//
//    @Query("SELECT f FROM Fine f WHERE f.member.id = :memberId AND f.paid = false")
//    List<Fine> findUnpaidFinesByMember(@Param("memberId") Long memberId);
//
//    @Query("SELECT br FROM Borrowing br ORDER BY br.borrowDate DESC")
//    List<Borrowing> findRecentBorrowings();
//
//    @Query("SELECT SUM(f.paidAmount) FROM Fine f WHERE f.member.id = :memberId AND f.paid = true")
//    BigDecimal totalFinesPaid(@Param("memberId") Long memberId);
//
//    @Query("""
//    SELECT DISTINCT m
//    FROM Member m
//    JOIN m.borrowings b
//    WHERE b.status = com.yourbank.enums.BorrowingStatus.OVERDUE
//""")
//    List<Member> findMembersWithOverdueBooks();
//
//
//    @Query("""
//    SELECT m.firstName, m.lastName, COUNT(b)
//    FROM Member m
//    JOIN m.borrowings b
//    GROUP BY m.id
//""")
//    List<Object[]> countBorrowingsByMember();
//
//
//    @Query("SELECT b FROM Book b WHERE b.borrowings IS EMPTY")
//    List<Book> findNeverBorrowedBooks();
//
//    @Query("""
//    SELECT b.title, COUNT(br)
//    FROM Borrowing br
//    JOIN br.book b
//    GROUP BY b.title
//    ORDER BY COUNT(br) DESC
//""")
//    List<Object[]> findTop5BorrowedBooks(Pageable pageable);
//
//    @Modifying
//    @Query("UPDATE Fine f SET f.paid = true, f.paidDate = CURRENT_TIMESTAMP WHERE f.id = :fineId")
//    void markFineAsPaid(@Param("fineId") Long fineId);
//
    //Exactly Match with String

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findByIsbn(String isbn);

    List<Book> findByTitleAndAuthor(String title, String author);

    List<Book> findByTitleOrAuthor(String title, String author);

    List<Book> findByTitleOrAuthorOrIsbn(String title, String author, String isbn);

    List<Book> findByAuthorOrTitle(String author, String title);

    List<Book> findByAuthorOrTitleOrIsbn(String author, String title, String isbn);

    List<Book> findByPublisher(String publisher);

    List<Book> findByPublisherOrTitle(String publisher, String title);

    List<Book> findByPublisherOrIsbn(String publisher, String isbn);

    List<Book> findByPublisherOrTitleOrIsbn(String publisher, String title, String isbn);

    List<Book> findByPublisherOrIsbnOrTitle(String publisher, String isbn, String title);

    List<Book> findByPublishedYear(Integer publishedYear);

    List<Book> findByPublishedYear(Integer publishedYear, Pageable pageable);

    List<Book> findByStatusAndAvailableCopiesGreaterThan(Integer status, Integer copies, Pageable pageable);

    List<Book> findByAvailableCopiesGreaterThan(Integer copies, Pageable pageable);

    //BetweenYear //Date Filter

    @Query("select b from Book b where b.publishedYear between :startYear and :endYear")
    List<Book> findByBooksPublishedBetween(@Param("startYear") LocalDate startYear,
                                           @Param("endYear") LocalDate endYear);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(concat('%', :titlePart,'%'))")
    List<Book> findByTitleContainingIgnoreCase(@Param("titlePart") String titlePart);

    Page<Book> findByStatus(BookStatus status, Pageable pageable);

    // Update the available Copies Increment or Decrement

    @Modifying
    @Transactional
    @Query("update Book b set b.availableCopies = b.availableCopies - :decrement where b.id = :id")
    int decrementAvailableCopies(@Param("id") Long id, @Param("decrement") Integer decrement);

    @Query("select b from Book b join fetch  b.borrowings where  b.id = :id")
    Book findByIdWithBorrowings(@Param("id") Long id);

    @Query("select count (b)from Book b where b.status = :status")
    Long countByStatus(@Param("status") BookStatus status);

    //Find Popular Books


    @Query("SELECT b FROM Book b WHERE b.id IN " +
        "(SELECT br.book.id FROM Borrowing br GROUP BY br.book.id " +
        "ORDER BY COUNT(br) DESC LIMIT 10)")
    List<Book> findMostBorrowedBooksJpql();

}
