package com.innowise.gateway.service;

import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.response.UserPhotoResponse;
import reactor.core.publisher.Mono;

public interface UserStoragePhotoGateway {

    Mono<UserPhotoResponse> saveUserPhoto(SaveUserPhotoRequest saveUserPhotoRequest);
}
