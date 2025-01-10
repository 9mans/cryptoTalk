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

//    @Scheduled(fixedRate = 60000)
    public void updatePrices() {
        try {
            List<Map<String, Object>> markets = upbitService.getMarketAll();

            List<String> marketCodes = markets.stream()
                    .map(market -> market.get("market").toString())
                    .filter(market -> market.startsWith("KRW-"))
                    .toList();

            List<Map<String, Object>> tickerData = upbitService.getTicker(marketCodes);

            for (Map<String, Object> market : markets) {
                String marketCode = market.get("market").toString();
                String korName = market.get("korean_name").toString();

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
