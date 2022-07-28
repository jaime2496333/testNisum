package com.test.nisum.exception;

import org.springframework.http.HttpStatus;

public class UserApiActionException extends RuntimeException {

    private HttpStatus status;

    public UserApiActionException(String message) {
        super(message);
    }

    public UserApiActionException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public UserApiActionException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
