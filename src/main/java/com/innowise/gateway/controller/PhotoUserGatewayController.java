package com.innowise.gateway.controller;

import com.innowise.gateway.service.UserPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("photo")
@RequiredArgsConstructor
@Slf4j
public class PhotoUserGatewayController {

    private final UserPhotoService userPhotoService;

    @GetMapping("{idPhoto}")
    public Mono<Void> getUserPhoto(@PathVariable("idPhoto") String idPhoto, ServerWebExchange serverWebExchange) {
        return userPhotoService.getUserPhoto(idPhoto,serverWebExchange)
                .doOnSuccess(success -> log.debug("getUserPhoto success"))
                .doOnError(error -> log.error("getUserPhoto error"));
    }

}
