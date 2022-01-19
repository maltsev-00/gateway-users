package com.innowise.gateway.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
public class ErrorDetails {
    LocalDateTime timestamp;
    String error;
}
