package com.example.cryptotalk.controller;

import com.example.cryptotalk.entity.CryptoPrice;
import com.example.cryptotalk.service.CryptoPriceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class CryptoPriceController {

    private final CryptoPriceService cryptoPriceService;

    public CryptoPriceController(CryptoPriceService cryptoPriceService) {
        this.cryptoPriceService = cryptoPriceService;
    }

    // 모든 가격 정보 조회
    public List<CryptoPrice> getAllPrice() {
        return cryptoPriceService.getAllPrice();
    }

    // 특정 가격 정보를 저장
    @PostMapping("/prices")
    public String savePrice(@RequestParam String market, @RequestParam BigDecimal price, @RequestParam String korName) {
        cryptoPriceService.saveOrUpdatePrice(market, price, korName);
        return "Price updated for market: " + market;
    }
}
