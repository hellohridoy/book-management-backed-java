package com.yourbank.service;

import com.yourbank.entity.Book;
import com.yourbank.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public List<Book> getAllBooksBetweenStartAndEnd(LocalDate start, LocalDate end) {
        if(start.equals(end)) {
            throw new RuntimeException("Need minimum One day");
        }
        if(start.isBefore(end)) {
            throw new RuntimeException("End should be before start");
        }
        if(start==null || end==null) {
            throw new RuntimeException("Start and end should not be null");
        }
        return bookRepository.findByBooksPublishedBetween(start, end);
    }
}
