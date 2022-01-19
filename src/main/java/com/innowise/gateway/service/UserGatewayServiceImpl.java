package com.innowise.gateway.service;

import com.innowise.gateway.exception.UsersInfoApiException;
import com.innowise.gateway.model.dto.UserDto;
import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.request.UserRequest;
import com.innowise.gateway.model.request.UserSaveRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UserGatewayServiceImpl implements UserGatewayService {

    private final WebClient userInfoClient;
    private final PhotoUserGateway photoUserGateway;

    public UserGatewayServiceImpl(@Qualifier("userInfo") WebClient userInfoClient, PhotoUserGateway photoUserGateway) {
        this.userInfoClient = userInfoClient;
        this.photoUserGateway = photoUserGateway;
    }

    @Override
    public Flux<UserDto> getUsers(UserRequest userRequest) {
        return userInfoClient.get()
                .uri(builder -> builder.queryParam("idUser", "{idUser}")
                        .queryParam("username", "{username}")
                        .queryParam("email", "{email}")
                        .queryParam("pageNo", "{pageNo}")
                        .queryParam("pageSize", "{pageSize}")
                        .build(userRequest.getIdUser(),
                                userRequest.getUsername(),
                                userRequest.getEmail(),
                                userRequest.getPageNo(),
                                userRequest.getPageSize()))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                        .flatMap(message -> Mono.error(new UsersInfoApiException(message))))
                .bodyToFlux(UserDto.class);
    }

    @Override
    public Mono<UUID> saveUser(UserSaveRequest userSaveRequest) {
        return userInfoClient.post()
                .body(BodyInserters.fromValue(userSaveRequest))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                        .flatMap(message -> Mono.error(new UsersInfoApiException(message))))
                .bodyToMono(UUID.class);
    }

    @Override
    public Mono<Void> deleteUser(UUID id) {
        return userInfoClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/{id}/").build(id))
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<UUID> saveUserPhoto(SaveUserPhotoRequest saveUserPhotoRequest) {
        return photoUserGateway.saveUserPhoto(saveUserPhotoRequest)
                .flatMap(userPhotoResponse ->
                        userInfoClient.put()
                                .uri(builder -> builder
                                        .queryParam("idUser", "{idUser}")
                                        .queryParam("idPhoto", "{idPhoto}")
                                        .build(saveUserPhotoRequest.getIdUser(),
                                                userPhotoResponse.getIdUserPhoto()))
                                .retrieve()
                                .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                                        .flatMap(message -> Mono.error(new UsersInfoApiException(message))))
                                .bodyToMono(UUID.class));
    }
}
