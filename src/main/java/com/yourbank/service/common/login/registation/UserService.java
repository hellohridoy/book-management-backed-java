package com.yourbank.service.common.login.registation;

import com.yourbank.dto.common.dto.RegistrationRequest;
import com.yourbank.dto.common.dto.UpdateUserRequest;
import com.yourbank.entity.User; // Make sure this imports YOUR User entity, not Spring Security's User

public interface UserService {

    User createUser(RegistrationRequest registerRequest);

    boolean existsByEmail(String email);

    // Changed return type from org.springframework.security.core.userdetails.User to your User entity
    User getCurrentUser();

    User updateUser(UpdateUserRequest request);
}
