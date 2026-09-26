package com.Security.Yellow.auth.service;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Security.Yellow.auth.dto.AuthResponse;
import com.Security.Yellow.auth.dto.LoginRequest;
import com.Security.Yellow.auth.dto.RegisterRequest;
import com.Security.Yellow.auth.dto.UserResponse;
import com.Security.Yellow.auth.entity.User;
import com.Security.Yellow.auth.exception.InvalidCredentialsException;
import com.Security.Yellow.auth.exception.UserAlreadyExistsException;
import com.Security.Yellow.auth.repository.UserRepository;
import com.Security.Yellow.auth.security.JwtUtil;
import com.Security.Yellow.auth.security.UserPrincipal;

@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameOrEmail(request.username(), request.email())) {
            throw new UserAlreadyExistsException();
        }

        User user = new User(request.username(), request.email(), passwordEncoder.encode(request.password()),
                DEFAULT_ROLE);
        User savedUser = userRepository.save(user);
        LOGGER.info("Registered account id={}", savedUser.getId());
        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtUtil.generateToken(principal.getUsername(), List.copyOf(principal.getRoles()));
            return new AuthResponse(token, "Bearer", jwtUtil.getExpirationMs() / 1000,
                    principal.getUsername(), Set.copyOf(principal.getRoles()));
        } catch (AuthenticationException exception) {
            LOGGER.warn("Authentication failed");
            throw new InvalidCredentialsException();
        }
    }
}