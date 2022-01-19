package com.innowise.gateway.controller;

import com.innowise.gateway.model.dto.UserDto;
import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.request.UserRequest;
import com.innowise.gateway.model.request.UserSaveRequest;
import com.innowise.gateway.service.UserGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("gateway/users")
@RequiredArgsConstructor
@Slf4j
public class UserGatewayController {

    private final UserGatewayService userGatewayService;

    @GetMapping
    public Flux<UserDto> getUsers(@Valid UserRequest userRequest) {
        return userGatewayService.getUsers(userRequest)
                .doOnComplete(() -> log.debug("getUsers success"))
                .doOnError(error -> log.error("getUsers error: {}", error.getMessage()));
    }

    @PostMapping
    public Mono<UUID> saveUser(@Valid @RequestBody UserSaveRequest userSaveRequest) {
        return userGatewayService.saveUser(userSaveRequest)
                .doOnSuccess(success -> log.debug("saveUser success"))
                .doOnError(error -> log.error("saveUser error: {}", error.getMessage()));
    }

    @DeleteMapping("/{idUser}")
    public Mono<Void> deleteUser(@PathVariable("idUser") UUID id) {
        return userGatewayService.deleteUser(id)
                .doOnSuccess(success -> log.debug("deleteUser success"))
                .doOnError(error -> log.error("deleteUser error: {}", error.getMessage()));
    }

    @PutMapping
    public Mono<UUID> saveUserPhoto(@Valid @RequestPart(name = "photo") FilePart filePart, @RequestParam UUID idUser) {
        return userGatewayService.saveUserPhoto(SaveUserPhotoRequest.builder()
                        .filePart(filePart)
                        .idUser(idUser)
                        .build())
                .doOnSuccess(success -> log.debug("saveUserPhoto success"))
                .doOnError(error -> log.error("saveUserPhoto error: {}", error.getMessage()));
    }
}
