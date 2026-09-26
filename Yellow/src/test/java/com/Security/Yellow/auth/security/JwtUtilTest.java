package com.Security.Yellow.auth.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtUtilTest {
    private static final String SECRET = "Yellow-test-secret-key-that-is-at-least-32-bytes-long";

    @Test
    void generatesTokenWithSubjectRolesIssuedAtAndExpiration() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);

        String token = jwtUtil.generateToken("alice", List.of("ROLE_USER"));

        assertTrue(jwtUtil.validateToken(token));
        assertEquals("alice", jwtUtil.extractUsername(token));
        assertEquals(List.of("ROLE_USER"), jwtUtil.extractRoles(token));
        assertNotNull(jwtUtil.extractAllClaims(token).getIssuedAt());
        assertNotNull(jwtUtil.extractAllClaims(token).getExpiration());
    }

    @Test
    void rejectsTokenSignedWithDifferentSecret() {
        JwtUtil jwtUtil = new JwtUtil(SECRET, 60_000);
        String token = Jwts.builder()
                .subject("alice")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor("Different-test-secret-key-that-is-also-32-bytes-long".getBytes()))
                .compact();

        assertFalse(jwtUtil.validateToken(token));
    }
}