package com.Security.Yellow.auth.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("Username or email is already registered");
    }
}