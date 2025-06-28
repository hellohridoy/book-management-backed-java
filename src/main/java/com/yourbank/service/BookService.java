package com.yourbank.service;

import com.yourbank.entity.Book;

import java.time.LocalDate;
import java.util.List;

public interface BookService {
    List<Book> getAllBooksBetweenStartAndEnd(LocalDate start, LocalDate end);
}
