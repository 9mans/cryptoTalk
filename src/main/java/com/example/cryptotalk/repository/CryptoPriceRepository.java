package com.example.cryptotalk.repository;

import com.example.cryptotalk.entity.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {

    // 마켓 코드로 가격 정보를 조회하는 메서드
    CryptoPrice findByMarket(String market);
}
