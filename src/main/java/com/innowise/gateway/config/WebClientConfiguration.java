package com.innowise.gateway.config;

import com.innowise.gateway.config.properties.UserInfoApplicationProperties;
import com.innowise.gateway.config.properties.UserPhotoStorageApplicationProperties;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfiguration {

    @Bean(name = "userInfoClient")
    public WebClient userInfoWebClient(UserInfoApplicationProperties userInfoApplicationProperties) {
        return getWebClient(userInfoApplicationProperties.getOption(), userInfoApplicationProperties.getReadTimeout(), userInfoApplicationProperties.getWriteTimeout(), userInfoApplicationProperties.getUrl());
    }

    @Bean(name = "userStoragePhotoClient")
    public WebClient userPhotoStorageWebClient(UserPhotoStorageApplicationProperties userPhotoStorageApplicationProperties) {
        return getWebClient(userPhotoStorageApplicationProperties.getOption(), userPhotoStorageApplicationProperties.getReadTimeout(), userPhotoStorageApplicationProperties.getWriteTimeout(), userPhotoStorageApplicationProperties.getUrl());
    }

    private WebClient getWebClient(int option, int readTimeout, int writeTimeout, String url) {
        final var tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, option)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS));
                });

        return WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }
}
