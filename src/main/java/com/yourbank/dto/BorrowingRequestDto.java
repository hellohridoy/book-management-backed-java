package com.yourbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingRequestDto {
    private Long userId;
    private Long bookId;
    private LocalDate borrowDate;
}
