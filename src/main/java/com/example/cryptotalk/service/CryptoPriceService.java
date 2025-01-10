package com.example.cryptotalk.service;

import com.example.cryptotalk.entity.CryptoPrice;
import com.example.cryptotalk.repository.CryptoPriceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CryptoPriceService {

    private final CryptoPriceRepository repository;

    public CryptoPriceService(CryptoPriceRepository repository) {
        this.repository = repository;
    }

    public void saveOrUpdatePrice(String market, BigDecimal price, String korName) {
        CryptoPrice cryptoPrice = repository.findByMarket(market);
        if (cryptoPrice == null) {
            cryptoPrice = new CryptoPrice();
            cryptoPrice.setMarket(market);
        }

        cryptoPrice.setKorName(korName);
        cryptoPrice.setPrice(price);
        cryptoPrice.setTimestamp(LocalDateTime.now());
        repository.save(cryptoPrice);
    }

    public List<CryptoPrice> getAllPrice() {
        return repository.findAll();
    }
}
