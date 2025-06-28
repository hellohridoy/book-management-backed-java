package com.yourbank.dto.common.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String name;

    public UserResponse(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
