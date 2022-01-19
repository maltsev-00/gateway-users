package com.innowise.gateway.model.request;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Value
@Builder
public class SaveUserPhotoRequest {
    FilePart filePart;
    UUID idUser;
}
