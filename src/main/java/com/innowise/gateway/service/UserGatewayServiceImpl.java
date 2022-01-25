package com.innowise.gateway.service;

import com.innowise.gateway.exception.UserInfoApplicationException;
import com.innowise.gateway.model.ErrorDetails;
import com.innowise.gateway.model.dto.UserDto;
import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.request.UserRequest;
import com.innowise.gateway.model.request.UserSaveRequest;
import com.innowise.gateway.model.response.UserPhotoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserGatewayServiceImpl implements UserGatewayService {

    private final WebClient userInfoClient;

    public UserGatewayServiceImpl(@Qualifier("userInfo") WebClient userInfoClient) {
        this.userInfoClient = userInfoClient;
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
                .exchangeToFlux(clientResponse -> {
                    HttpStatus status = clientResponse.statusCode();
                    if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status) || status.isError()) {
                        return clientResponse.bodyToFlux(String.class)
                                .flatMap(errorMessage -> Mono.error(new UserInfoApplicationException(status)));
                    }
                    return clientResponse.bodyToFlux(UserDto.class);
                });
    }

    @Override
    public Mono<UUID> saveUser(UserSaveRequest userSaveRequest) {
        return userInfoClient.post()
                .body(BodyInserters.fromValue(userSaveRequest))
                .exchangeToMono(clientResponse -> {
                    HttpStatus status = clientResponse.statusCode();
                    if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status) || status.isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new UserInfoApplicationException(status)));
                    }
                    return clientResponse.bodyToMono(UUID.class);
                });
    }

    @Override
    public Mono<Void> deleteUser(UUID id) {
        return userInfoClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/{id}/").build(id))
                .exchangeToMono(clientResponse -> {
                    HttpStatus status = clientResponse.statusCode();
                    if (HttpStatus.NOT_FOUND.equals(status)) {
                        return clientResponse.bodyToMono(ErrorDetails.class)
                                .flatMap(errorDetails -> Mono.error(new UserInfoApplicationException(status, errorDetails.getError())));
                    }
                    if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status) || status.isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new UserInfoApplicationException(status)));
                    }
                    return clientResponse.bodyToMono(Void.class);
                });
    }

    @Override
    public Mono<UUID> saveUserPhoto(UserPhotoResponse userPhotoResponseMono, SaveUserPhotoRequest saveUserPhotoRequest) {
        return userInfoClient.put()
                .uri(builder -> builder
                        .queryParam("idUser", "{idUser}")
                        .queryParam("idPhoto", "{idPhoto}")
                        .build(saveUserPhotoRequest.getIdUser(),
                                userPhotoResponseMono.getIdUserPhoto()))
                .exchangeToMono(clientResponse -> {
                    HttpStatus status = clientResponse.statusCode();
                    if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status) || status.isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new UserInfoApplicationException(HttpStatus.INTERNAL_SERVER_ERROR)));
                    }
                    return clientResponse.bodyToMono(UUID.class);
                });
    }

    @Override
    public Flux<UUID> saveUsers(List<UserSaveRequest> userSaveRequests) {
        return userInfoClient.post()
                .uri(uriBuilder -> uriBuilder.path("/list").build())
                .body(BodyInserters.fromValue(userSaveRequests))
                .retrieve()
                .bodyToFlux(UUID.class);
    }

}
