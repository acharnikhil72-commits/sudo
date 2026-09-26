package com.Security.Yellow.auth.dto;

import java.util.Set;

public record AuthResponse(String token, String tokenType, long expiresIn, String username, Set<String> roles) {
}