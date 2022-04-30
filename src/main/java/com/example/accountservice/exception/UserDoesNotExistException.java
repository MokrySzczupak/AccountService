package com.example.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found!")
public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException() {
        super();
    }
}
