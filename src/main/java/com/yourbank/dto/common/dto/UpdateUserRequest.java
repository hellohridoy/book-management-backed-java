package com.yourbank.dto.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @Email
    @Size(max = 50)
    private String email;

    @Size(min = 6, max = 40)
    private String password; // Required when changing email
}
