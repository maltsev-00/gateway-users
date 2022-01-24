package com.innowise.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserInfoApplicationException extends ResponseStatusException {


    public UserInfoApplicationException(HttpStatus status) {
        super(status);
    }

    public UserInfoApplicationException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public UserInfoApplicationException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public UserInfoApplicationException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }

}
