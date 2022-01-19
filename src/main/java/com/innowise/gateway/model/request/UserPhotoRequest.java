package com.innowise.gateway.model.request;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UserPhotoRequest {
    UUID idUser;
    String idPhoto;
}
