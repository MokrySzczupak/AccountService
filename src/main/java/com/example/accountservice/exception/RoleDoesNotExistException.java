package com.example.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Role not found!")
public class RoleDoesNotExistException extends RuntimeException {
    public RoleDoesNotExistException() {
        super();
    }
}
