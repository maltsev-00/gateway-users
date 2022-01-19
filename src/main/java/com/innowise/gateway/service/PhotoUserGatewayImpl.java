package com.innowise.gateway.service;

import com.innowise.gateway.exception.UserPhotoStorageApiException;
import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.response.UserPhotoResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Service
public class PhotoUserGatewayImpl implements PhotoUserGateway, UserPhotoService {

    private final WebClient userStoragePhotoClient;

    public PhotoUserGatewayImpl(@Qualifier("userStoragePhoto") WebClient userStoragePhotoClient) {
        this.userStoragePhotoClient = userStoragePhotoClient;
    }

    @Override
    public Mono<UserPhotoResponse> saveUserPhoto(SaveUserPhotoRequest saveUserPhotoRequest) {
        return userStoragePhotoClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder(saveUserPhotoRequest.getFilePart())))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                        .flatMap(message -> Mono.error(new UserPhotoStorageApiException(message))))
                .bodyToMono(UserPhotoResponse.class);
    }

    @Override
    public Mono<Void> getUserPhoto(String idPhoto, ServerWebExchange serverWebExchange) {
        return userStoragePhotoClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{id}/").build(idPhoto))
                .retrieve()
                .bodyToMono(Void.class);
    }

    private MultiValueMap<String, HttpEntity<?>> builder(FilePart filePart) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("photo", filePart);
        return builder.build();
    }
}
