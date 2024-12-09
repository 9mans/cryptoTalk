package com.example.cryptotalk.scheduler;

import com.example.cryptotalk.service.CryptoPriceService;
import com.example.cryptotalk.service.UpbitService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class PriceUpdateScheduler {

    private final UpbitService upbitService;
    private final CryptoPriceService cryptoPriceService;

    public PriceUpdateScheduler(UpbitService upbitService, CryptoPriceService cryptoPriceService) {
        this.cryptoPriceService = cryptoPriceService;
        this.upbitService = upbitService;
    }

    // 매 1분 마다 실행
    @Scheduled(fixedRate = 60000)
    public void updatePrices() {
        try {
            // 모든 마켓 정보 가져오기
            List<Map<String, Object>> markets = upbitService.getMarketAll();

            // 원화 마켓 코드만 추출
            List<String> marketCodes = markets.stream()
                    .map(market -> market.get("market").toString())
                    .filter(market -> market.startsWith("KRW-"))
                    .toList();

            // 각 마켓의 현재가 가져오기
            List<Map<String, Object>> tickerData = upbitService.getTicker(marketCodes);

            // DB 업데이트
            for (Map<String, Object> market : markets) {
                String marketCode = market.get("market").toString();
                String korName = market.get("korean_name").toString();

                // 현재가 정보
                Map<String, Object> ticker = tickerData.stream()
                        .filter(t -> t.get("market").toString().equals(marketCode))
                        .findFirst()
                        .orElse(null);

                if (ticker != null) {
                    BigDecimal price = new BigDecimal(ticker.get("trade_price").toString());
                    cryptoPriceService.saveOrUpdatePrice(marketCode, price, korName);
                }
                System.out.println("Market: " + market.get("market") + ", Korean Name: " + market.get("korean_name"));
            }

            System.out.println("Price update completed for all markets");

        } catch (Exception e) {
            System.err.println("error occurred during price update" + e.getMessage());
        }
    }
}
