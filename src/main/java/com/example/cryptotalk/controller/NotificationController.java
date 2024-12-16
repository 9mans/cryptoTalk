package com.example.cryptotalk.controller;

import com.example.cryptotalk.entity.NotificationCondition;
import com.example.cryptotalk.repository.NotificationConditionRepository;
import com.example.cryptotalk.service.UpbitService;
import com.example.cryptotalk.util.AESUtil;
import io.jsonwebtoken.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class NotificationController {

    private final UpbitService upbitService;
    private final NotificationConditionRepository conditionRepository;
    private final AESUtil aesUtil;

    public NotificationController(NotificationConditionRepository conditionRepository, AESUtil aesUtil, UpbitService upbitService) {
        this.conditionRepository = conditionRepository;
        this.aesUtil = aesUtil;
        this.upbitService = upbitService;
    }

    @GetMapping("/notifications/new")
    public String showNotificationForm(Model model) {

        List<Map<String, Object>> markets = upbitService.getMarketAll();

        List<Map<String, String>> marketOptions = markets.stream()
                        .filter(market -> market.get("market").toString().startsWith("KRW-"))
                        .map(market -> Map.of(
                                "market", market.get("market").toString(),
                                "korean_name", market.get("korean_name").toString()
                        ))
                        .toList();

        model.addAttribute("marketOptions", marketOptions);
        model.addAttribute("notification", new NotificationCondition());

        return "notification-form";
    }


    @PostMapping("/notifications")
    public String addNotification(NotificationCondition condition) {

        try {
            condition.setPhoneNumber(aesUtil.encrypt(condition.getPhoneNumber()));
            condition.setActive(true);
            condition.setCreatedAt(LocalDateTime.now());
            conditionRepository.save(condition);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/notifications/new?error";
        }

        return "redirect:/notifications/new?success";
    }
}
