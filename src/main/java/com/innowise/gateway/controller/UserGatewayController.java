package com.innowise.gateway.controller;

import com.innowise.gateway.model.dto.UserDto;
import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.request.UserRequest;
import com.innowise.gateway.model.request.UserSaveRequest;
import com.innowise.gateway.service.UserInfoAndUserPhotoStorageFactoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("gateway/users")
@RequiredArgsConstructor
@Slf4j
public class UserGatewayController {

    private final UserInfoAndUserPhotoStorageFactoryService userInfoAndUserPhotoStorageFactoryService;

    @GetMapping
    public Flux<UserDto> getUsers(@Valid UserRequest userRequest) {
        return userInfoAndUserPhotoStorageFactoryService.getUsers(userRequest)
                .doOnComplete(() -> log.debug("getUsers success"))
                .doOnError(error -> log.error("getUsers error: {}", error.getMessage()));
    }

    @PostMapping
    public Mono<UUID> saveUser(@Valid @RequestBody UserSaveRequest userSaveRequest) {
        return userInfoAndUserPhotoStorageFactoryService.saveUser(userSaveRequest)
                .doOnSuccess(success -> log.debug("saveUser success"))
                .doOnError(error -> log.error("saveUser error: {}", error.getMessage()));
    }

    @DeleteMapping("/{idUser}")
    public Mono<Void> deleteUser(@PathVariable("idUser") UUID id) {
        return userInfoAndUserPhotoStorageFactoryService.deleteUser(id)
                .doOnSuccess(success -> log.debug("deleteUser success"))
                .doOnError(error -> log.error("deleteUser error: {}", error.getMessage()));
    }

    @PutMapping
    public Mono<UUID> saveUserPhoto(@Valid @RequestPart(name = "photo") FilePart filePart, @RequestParam UUID idUser) {
        return userInfoAndUserPhotoStorageFactoryService.saveUserPhoto(SaveUserPhotoRequest.builder()
                        .filePart(filePart)
                        .idUser(idUser)
                        .build())
                .doOnSuccess(success -> log.debug("saveUserPhoto success"))
                .doOnError(error -> log.error("saveUserPhoto error: {}", error.getMessage()));
    }

    @PostMapping("/list")
    public Flux<UUID> saveUsers(@Valid @RequestBody List<UserSaveRequest> userSaveRequests) {
        return userInfoAndUserPhotoStorageFactoryService.saveUsers(userSaveRequests)
                .doOnComplete(() -> log.debug("saveUsers() success"))
                .doOnError(error -> log.error("saveUsers() error"));
    }
}
