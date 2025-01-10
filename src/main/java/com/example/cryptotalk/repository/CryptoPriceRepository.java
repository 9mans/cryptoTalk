package com.example.cryptotalk.repository;

import com.example.cryptotalk.entity.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {

    CryptoPrice findByMarket(String market);
}
