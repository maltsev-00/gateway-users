package com.innowise.gateway.exception;


import com.innowise.gateway.model.ErrorDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;


@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String INTERNAL_SERVER_MESSAGE = "Invalid response from the server";

    @ExceptionHandler({ConstraintViolationException.class, WebExchangeBindException.class})
    public Mono<ResponseEntity<ErrorDetails>> handleBadRequestStatusException(RuntimeException exception) {
        String message = exception.getMessage();
        log.error(message);
        return buildResponse(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UsersInfoApiException.class, UserPhotoStorageApiException.class, JsonParserException.class})
    public Mono<ResponseEntity<ErrorDetails>> handleNotFoundResourceStatusException(UsersInfoApiException exception) {
        String message = exception.getMessage();
        log.error(message);
        return buildResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ErrorDetails>> handleInternalException(Throwable exception) {
        String message = exception.getMessage();
        if (message.isEmpty()) {
            message = INTERNAL_SERVER_MESSAGE;
        }
        log.error(message);
        return buildResponse(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Mono<ResponseEntity<ErrorDetails>> buildResponse(String message, HttpStatus httpStatus) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .timestamp(LocalDateTime.now())
                .error(message)
                .build();
        return Mono.just(ResponseEntity.status(httpStatus).body(errorDetails));
    }
}
