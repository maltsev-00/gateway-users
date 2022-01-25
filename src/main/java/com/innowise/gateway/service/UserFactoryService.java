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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserFactoryService {

    private final UserGatewayService userGatewayService;
    private final AuditActionsUserKafkaProducer auditActionsUserKafkaProducer;
    private final UserStoragePhotoGateway userStoragePhotoGateway;

    public Flux<UserDto> getUsers(UserRequest userRequest) {
        return userGatewayService.getUsers(userRequest)
                .doOnNext(response -> auditActionsUserKafkaProducer.send(
                                AuditUserDto.builder()
                                        .action("getUsers")
                                        .response(response.toString())
                                        .build())
                        .subscribe());
    }

    public Mono<UUID> saveUser(UserSaveRequest userSaveRequest) {
        return userGatewayService.saveUser(userSaveRequest)
                .doOnSuccess(response -> auditActionsUserKafkaProducer.send(
                                AuditUserDto.builder()
                                        .action("saveUser")
                                        .response(response.toString())
                                        .build())
                        .subscribe());
    }

    public Mono<Void> deleteUser(UUID id) {
        return userGatewayService.deleteUser(id)
                .doOnSuccess(response -> auditActionsUserKafkaProducer.send(
                                AuditUserDto.builder()
                                        .action("deleteUser")
                                        .response("VOID")
                                        .build())
                        .subscribe());
    }

    public Mono<UUID> saveUserPhoto(SaveUserPhotoRequest saveUserPhotoRequest) {
        return userStoragePhotoGateway.saveUserPhoto(saveUserPhotoRequest)
                .flatMap(userPhotoResponse -> userGatewayService.saveUserPhoto(userPhotoResponse, saveUserPhotoRequest))
                .doOnSuccess(success -> auditActionsUserKafkaProducer.send(
                                AuditUserDto.builder()
                                        .action("saveUserPhoto")
                                        .response(success.toString())
                                        .build())
                        .subscribe());
    }

    public Flux<UUID> saveUsers(List<UserSaveRequest> userSaveRequests) {
        return userGatewayService.saveUsers(userSaveRequests)
                .doOnNext(response -> auditActionsUserKafkaProducer.send(
                                AuditUserDto.builder()
                                        .action("saveUsers")
                                        .response(response.toString())
                                        .build())
                        .subscribe());
    }
}
