package com.yourbank.dto;

import com.yourbank.enums.BookStatus;
import lombok.*;

@Data
public class AvailableBookSummaryDto {
    private Long id;
    private String title;
    private Integer totalCopies;
    private BookStatus status;

    public AvailableBookSummaryDto(Long id, String title, Integer totalCopies, BookStatus status) {
        this.id = id;
        this.title = title;
        this.totalCopies = totalCopies;
        this.status = status;
    }
}
