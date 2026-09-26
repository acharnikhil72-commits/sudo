package com.Security.Yellow.auth.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException() {
        super("Authentication token has expired");
    }
}