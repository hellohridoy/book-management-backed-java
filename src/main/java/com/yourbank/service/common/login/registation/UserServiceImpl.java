package com.yourbank.service.common.login.registation;

import com.yourbank.dto.common.dto.RegistrationRequest;
import com.yourbank.dto.common.dto.UpdateUserRequest;
import com.yourbank.entity.User;
import com.yourbank.exceptions.LibraryException;
import com.yourbank.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(RegistrationRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new LibraryException.InvalidOperationException("Email is already registered");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());

        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new LibraryException.AuthenticationFailedException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        // Since your User entity implements UserDetails, it should be the principal
        if (principal instanceof User) {
            return (User) principal;
        } else if (principal instanceof String) {
            // Fallback: if principal is username/email string
            return userRepository.findByEmail((String) principal)
                .orElseThrow(() -> new LibraryException.EntityNotFoundException("User not found"));
        }

        throw new LibraryException.AuthenticationFailedException("Invalid authentication principal");
    }

    @Override
    public User updateUser(UpdateUserRequest request) {
        User currentUser = getCurrentUser();

        currentUser.setName(request.getName());

        // If changing email, verify password
        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail())) {
            if (!passwordEncoder.matches(request.getPassword(), currentUser.getPassword())) {
                throw new LibraryException.InvalidOperationException("Password is incorrect");
            }
            currentUser.setEmail(request.getEmail());
        }

        return userRepository.save(currentUser);
    }
}
