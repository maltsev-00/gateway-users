package com.innowise.gateway.model.request;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Min;
import java.util.UUID;


@Value
@Builder
public class UserRequest {
    int pageNo;
    @Min(1)
    int pageSize;
    UUID idUser;
    String email;
    String username;
}
