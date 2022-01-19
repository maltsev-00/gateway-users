package com.innowise.gateway.service;

import com.innowise.gateway.model.dto.UserDto;
import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.request.UserRequest;
import com.innowise.gateway.model.request.UserSaveRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserGatewayService {

    Flux<UserDto> getUsers(UserRequest userRequest);

    Mono<UUID> saveUser(UserSaveRequest userSaveRequest);

    Mono<Void> deleteUser(UUID id);

    Mono<UUID> saveUserPhoto(SaveUserPhotoRequest saveUserPhotoRequest);
}