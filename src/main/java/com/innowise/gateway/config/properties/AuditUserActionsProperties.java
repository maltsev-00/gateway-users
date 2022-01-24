package com.innowise.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("kafka.properties")
@Data
public class AuditUserActionsProperties {
    private String topic;
}
