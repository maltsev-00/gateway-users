package com.innowise.gateway.controller;

import com.innowise.gateway.model.dto.UserDto;
import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.request.UserRequest;
import com.innowise.gateway.model.request.UserSaveRequest;
import com.innowise.gateway.service.UserGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("gateway/users")
@RequiredArgsConstructor
public class UserGatewayController {

    private final UserGatewayService userGatewayService;

    @GetMapping
    public Flux<UserDto> getUsers(@Valid UserRequest userRequest) {
        return userGatewayService.getUsers(userRequest);
    }

    @PostMapping
    public Mono<UUID> saveUser(@Valid @RequestBody UserSaveRequest userSaveRequest) {
        return userGatewayService.saveUser(userSaveRequest);
    }

    @DeleteMapping("/{idUser}")
    public Mono<Void> deleteUser(@PathVariable("idUser") UUID id) {
        return userGatewayService.deleteUser(id);
    }

    @PutMapping
    public Mono<UUID> saveUserPhoto(@Valid @RequestPart(name = "photo") FilePart filePart, @RequestParam UUID idUser) {
        return userGatewayService.saveUserPhoto(SaveUserPhotoRequest.builder()
                .filePart(filePart)
                .idUser(idUser)
                .build());
    }
}
