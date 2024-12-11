package com.example.cryptotalk.service;

import com.example.cryptotalk.entity.NotificationCondition;
import com.example.cryptotalk.repository.NotificationConditionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationConditionRepository conditionRepository;

    public NotificationService(NotificationConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }

    public void evaluateConditions(String market, BigDecimal currentPrice) {

        List<NotificationCondition> conditions = conditionRepository.findByIsActiveTrue();

        for (NotificationCondition condition : conditions) {
            if (!condition.getMarket().equals(market)) {
                continue;
            }
            boolean isConditionMet = false;

            if (condition.getDirection() == NotificationCondition.Direction.UP && currentPrice.compareTo(condition.getTargetPrice()) > 0) {
                isConditionMet = true;
            } else if (condition.getDirection() == NotificationCondition.Direction.DOWN && currentPrice.compareTo(condition.getTargetPrice()) < 0) {
                isConditionMet = true;
            } else if (condition.getDirection() == NotificationCondition.Direction.SAME && currentPrice.compareTo(condition.getTargetPrice()) == 0) {
                isConditionMet = true;
            }

            if (isConditionMet) {
                sendNotification(condition, currentPrice);
                condition.setActive(false);
                conditionRepository.save(condition);
            }
        }
    }

    private void sendNotification(NotificationCondition condition, BigDecimal currentPrice) {
        System.out.println(condition.toString());
    }
}
