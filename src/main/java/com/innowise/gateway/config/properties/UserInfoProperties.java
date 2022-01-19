package com.innowise.gateway.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("user.info")
@Data
public class UserInfoProperties {
    private String url;
    private int readTimeout;
    private int writeTimeout;
    private int option;
}
