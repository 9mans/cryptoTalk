package com.example.cryptotalk.controller;

import com.example.cryptotalk.service.UpbitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UpbitController {

    private final UpbitService upbitService;

    public UpbitController(UpbitService upbitService) {
        this.upbitService = upbitService;
    }

    @GetMapping("/markets")
    public List<Map<String, Object>> getAllMarkets() {
        return upbitService.getMarketAll();
    }

    @GetMapping("/ticker")
    public List<Map<String, Object>> getTicker(@RequestParam List<String> markets) {
        return upbitService.getTicker(markets);
    }
}
