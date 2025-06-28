package com.yourbank.controller;

import com.yourbank.config.JwtTokenProvider;
import com.yourbank.dto.common.dto.*;
import com.yourbank.entity.PasswordResetToken;
import com.yourbank.entity.User;
import com.yourbank.exceptions.LibraryException;
import com.yourbank.repository.PasswordResetTokenRepository;
import com.yourbank.repository.UserRepository;
import com.yourbank.service.common.login.registation.EmailService;
import com.yourbank.service.common.login.registation.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenUtil;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenUtil,
                          UserService userService,
                          EmailService emailService,
                          PasswordEncoder passwordEncoder,
                          PasswordResetTokenRepository tokenRepository,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Since User implements UserDetails, it will be the principal
        User user = (User) authentication.getPrincipal();

        // Generate JWT token
        String jwt = jwtTokenUtil.generateToken(authentication);
        JwtResponse jwtResponse = new JwtResponse(jwt, user.getId(), user.getEmail(), user.getName());

        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody RegistrationRequest registerRequest) {
        User user = userService.createUser(registerRequest);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully!", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> LibraryException.notFound("User", Long.valueOf(request.getEmail())));

        // Delete any existing tokens for this user
        tokenRepository.deleteByUserId(user.getId());

        // Create new token
        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(2)); // 2 hours expiration
        tokenRepository.save(token);

        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());

        return ResponseEntity.ok(ApiResponse.success("Password reset instructions sent to your email", null));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByToken(request.getToken())
            .orElseThrow(() -> new LibraryException.InvalidOperationException("Invalid token"));

        if (token.isUsed()) {
            throw new LibraryException.InvalidOperationException("Token already used");
        }

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new LibraryException.InvalidOperationException("Token expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        emailService.sendPasswordChangedNotification(user.getEmail());

        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        User currentUser = userService.getCurrentUser();

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new LibraryException.InvalidOperationException("Current password is incorrect");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        emailService.sendPasswordChangedNotification(currentUser.getEmail());

        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        UserResponse userResponse = new UserResponse(
            currentUser.getId(),
            currentUser.getEmail(),
            currentUser.getName()
        );

        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userResponse));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<String>> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        User currentUser = userService.getCurrentUser();

        currentUser.setName(request.getName());

        // If changing email, verify password
        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail())) {
            if (!passwordEncoder.matches(request.getPassword(), currentUser.getPassword())) {
                throw new LibraryException.InvalidOperationException("Password is incorrect");
            }
            currentUser.setEmail(request.getEmail());
        }

        userRepository.save(currentUser);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", null));
    }
}
