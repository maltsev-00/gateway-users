package com.innowise.gateway.service;

import com.innowise.gateway.model.dto.AuditUserDto;
import com.innowise.gateway.model.dto.UserDto;
import com.innowise.gateway.model.request.SaveUserPhotoRequest;
import com.innowise.gateway.model.request.UserRequest;
import com.innowise.gateway.model.request.UserSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInfoAndUserPhotoStorageFactoryService {

    private final UserGatewayService userGatewayService;
    private final AuditActionsUserKafkaProducer auditActionsUserKafkaProducer;
    private final UserStoragePhotoGateway userStoragePhotoGateway;

    public Flux<UserDto> getUsers(UserRequest userRequest) {
        return userGatewayService.getUsers(userRequest)
                .collectList()
                .doOnSuccess(users ->
                        auditActionsUserKafkaProducer.send(
                                        AuditUserDto.builder()
                                                .action("getUsers")
                                                .body("Users size list: " + users.size())
                                                .date(Timestamp.valueOf(LocalDateTime.now()))
                                                .build())
                                .subscribe())
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<UUID> saveUser(UserSaveRequest userSaveRequest) {
        return userGatewayService.saveUser(userSaveRequest)
                .doOnSuccess(response ->
                        auditActionsUserKafkaProducer.send(
                                        AuditUserDto.builder()
                                                .action("saveUser")
                                                .body(response.toString())
                                                .date(Timestamp.valueOf(LocalDateTime.now()))
                                                .build())
                                .subscribe());
    }

    public Mono<Void> deleteUser(UUID id) {
        return userGatewayService.deleteUser(id)
                .doOnSuccess(response ->
                        auditActionsUserKafkaProducer.send(AuditUserDto.builder()
                                        .action("deleteUser")
                                        .body("VOID")
                                        .date(Timestamp.valueOf(LocalDateTime.now()))
                                        .build())
                                .subscribe());
    }

    public Mono<UUID> saveUserPhoto(SaveUserPhotoRequest saveUserPhotoRequest) {
        return userStoragePhotoGateway.savePhoto(saveUserPhotoRequest)
                .flatMap(userPhotoResponse -> userGatewayService.saveUserPhoto(userPhotoResponse, saveUserPhotoRequest))
//                .onErrorMap(error -> new UserPhotoStorageApiException(error.getMessage())) delete photo and new exception
                .doOnSuccess(success ->
                        auditActionsUserKafkaProducer.send(
                                        AuditUserDto.builder()
                                                .action("saveUserPhoto")
                                                .body(success.toString())
                                                .date(Timestamp.valueOf(LocalDateTime.now()))
                                                .build())
                                .subscribe());
    }

    public Flux<UUID> saveUsers(List<UserSaveRequest> userSaveRequests) {
        return userGatewayService.saveUsers(userSaveRequests)
                .doOnNext(response ->
                        auditActionsUserKafkaProducer.send(
                                        AuditUserDto.builder()
                                                .action("saveUsers")
                                                .body(response.toString())
                                                .date(Timestamp.valueOf(LocalDateTime.now()))
                                                .build())
                                .subscribe());
    }
}
