package com.yourbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
    private Long id;
    private String title;
    private String isbn;
    private String publisher;
    private String status;
}
