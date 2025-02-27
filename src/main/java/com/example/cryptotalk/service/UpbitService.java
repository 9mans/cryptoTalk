package com.example.cryptotalk.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class UpbitService {

    private final WebClient webClient;

    public UpbitService(WebClient webClient) {
        this.webClient = webClient;
    }


    public List<Map<String, Object>> getMarketAll() {
        return webClient.get()
                .uri("/market/all")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
    }


    /**
     * 특정 마켓 정보만 받아오는 메서드
     * @param markets 요청할 마켓 코드 리스트 KRW-BTC, KRW-ETH
     * @return 요청된 마켓의 현재가 정보 리스트
     */
    public List<Map<String, Object>> getTicker(List<String> markets) {
        String marketParam = String.join(",", markets);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ticker")
                        .queryParam("markets", marketParam)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
    }
}