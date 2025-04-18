package com.vaultapp.personal_data_vault.service;

import com.vaultapp.personal_data_vault.dto.AuthResponse;
import com.vaultapp.personal_data_vault.dto.LoginRequest;
import com.vaultapp.personal_data_vault.dto.RegisterRequest;
import com.vaultapp.personal_data_vault.entity.User;
import com.vaultapp.personal_data_vault.entity.UserRole;
import com.vaultapp.personal_data_vault.repository.UserRepository;
import com.vaultapp.personal_data_vault.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        logger.info("Attempting to register user with email: {}", request.email());

        if (userRepo.findByEmail(request.email()).isPresent()) {
            logger.warn("Registration failed. Email already exists: {}", request.email());
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(UserRole.USER);
        userRepo.save(user);

        logger.info("User registered successfully with email: {}", request.email());

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Attempting login for user email: {}", request.email());

        User user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> {
                    logger.error("Login failed. User not found for email: {}", request.email());
                    return new UsernameNotFoundException("User not found");
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            logger.warn("Login failed due to invalid credentials for email: {}", request.email());
            throw new BadCredentialsException("Invalid credentials");
        }

        logger.info("Login successful for email: {}", request.email());

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
