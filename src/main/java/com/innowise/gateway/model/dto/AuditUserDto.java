package com.innowise.gateway.model.dto;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;

@Value
@Builder
public class AuditUserDto {
    Integer id;
    String action;
    String body;
    Timestamp date;

}
