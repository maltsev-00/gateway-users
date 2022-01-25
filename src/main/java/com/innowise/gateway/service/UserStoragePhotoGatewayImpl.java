package com.innowise.gateway.service;

import com.innowise.gateway.exception.UserPhotoStorageApiException;
import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.response.PhotoResponse;
import com.innowise.gateway.model.response.UserPhotoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class UserStoragePhotoGatewayImpl implements UserStoragePhotoGateway, UserPhotoService {

    private final WebClient userStoragePhotoClient;

    @Override
    public Mono<UserPhotoResponse> savePhoto(SaveUserPhotoRequest saveUserPhotoRequest) {
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
                .bodyToMono(PhotoResponse.class)
                .flatMap(response -> {
                    DataBuffer buffer = serverWebExchange.getResponse().bufferFactory().wrap(response.getBytes());
                    return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
                });
    }

    private MultiValueMap<String, HttpEntity<?>> builder(FilePart filePart) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("photo", filePart);
        return builder.build();
    }
}
