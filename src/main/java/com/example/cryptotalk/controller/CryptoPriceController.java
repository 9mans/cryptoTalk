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

    public List<CryptoPrice> getAllPrice() {
        return cryptoPriceService.getAllPrice();
    }

    @PostMapping("/prices")
    public String savePrice(@RequestParam String market, @RequestParam BigDecimal price, @RequestParam String korName) {
        cryptoPriceService.saveOrUpdatePrice(market, price, korName);
        return "Price updated for market: " + market;
    }
}
