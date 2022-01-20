package com.innowise.gateway.model.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuditUserDto {
    Integer id;
    String action;
    String response;
}
