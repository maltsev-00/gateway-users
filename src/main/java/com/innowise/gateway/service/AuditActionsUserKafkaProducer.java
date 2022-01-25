package com.innowise.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.gateway.config.properties.AuditUserActionsProperties;
import com.innowise.gateway.exception.JsonParserException;
import com.innowise.gateway.model.dto.AuditUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditActionsUserKafkaProducer {

    private final ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate;
    private final AuditUserActionsProperties auditUserActionsProperties;
    private final ObjectMapper objectMapper;

    public Mono<Void> send(AuditUserDto auditUserDto) {
        try {
            return reactiveKafkaProducerTemplate.send(auditUserActionsProperties.getTopic(), objectMapper.writeValueAsString(auditUserDto))
                    .doOnSuccess(senderResult -> log.info("sent {} ", auditUserDto))
                    .then();
        } catch (JsonProcessingException e) {
            throw new JsonParserException(e.getMessage());
        }
    }
}
