package com.Security.Yellow.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Security.Yellow.auth.dto.AuthResponse;
import com.Security.Yellow.auth.dto.LoginRequest;
import com.Security.Yellow.auth.dto.RegisterRequest;
import com.Security.Yellow.auth.dto.UserResponse;
import com.Security.Yellow.auth.entity.User;
import com.Security.Yellow.auth.exception.UserAlreadyExistsException;
import com.Security.Yellow.auth.repository.UserRepository;
import com.Security.Yellow.auth.security.JwtUtil;
import com.Security.Yellow.auth.security.UserPrincipal;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerHashesPasswordAndAssignsDefaultRole() {
        when(userRepository.existsByUsernameOrEmail("alice", "alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("very-secure-password")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = authService.register(
                new RegisterRequest("alice", "alice@example.com", "very-secure-password"));

        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertEquals("bcrypt-hash", savedUser.getValue().getPassword());
        assertEquals("ROLE_USER", savedUser.getValue().getRole());
        assertEquals("alice", result.username());
    }

    @Test
    void registerRejectsExistingUsernameOrEmail() {
        when(userRepository.existsByUsernameOrEmail("alice", "alice@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(
                new RegisterRequest("alice", "alice@example.com", "very-secure-password")));
    }

    @Test
    void loginReturnsBearerTokenAndRoles() {
        UserPrincipal principal = new UserPrincipal(
                new User("alice", "alice@example.com", "bcrypt-hash", "ROLE_USER"));
        Authentication authenticated = UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticated);
        when(jwtUtil.generateToken("alice", java.util.List.of("ROLE_USER"))).thenReturn("signed.jwt.token");
        when(jwtUtil.getExpirationMs()).thenReturn(3_600_000L);

        AuthResponse result = authService.login(new LoginRequest("alice", "very-secure-password"));

        assertEquals("signed.jwt.token", result.token());
        assertEquals("Bearer", result.tokenType());
        assertEquals(3600, result.expiresIn());
        assertEquals(Set.of("ROLE_USER"), result.roles());
    }
}