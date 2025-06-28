package com.yourbank.service;

import com.yourbank.dto.AvailableBookSummaryDto;
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


}
