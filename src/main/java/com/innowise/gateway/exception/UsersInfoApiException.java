package com.innowise.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UsersInfoApiException extends ResponseStatusException {


    public UsersInfoApiException(HttpStatus status) {
        super(status);
    }

    public UsersInfoApiException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public UsersInfoApiException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public UsersInfoApiException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }

}
