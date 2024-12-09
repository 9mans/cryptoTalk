package com.example.cryptotalk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    // 빌더 패턴을 사용하여 모든 요청에 대한 기본 url 설정
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.upbit.com/v1")
                .build();
    }
}
