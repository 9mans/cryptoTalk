package com.example.cryptotalk.controller;

import com.example.cryptotalk.entity.NotificationCondition;
import com.example.cryptotalk.repository.CryptoPriceRepository;
import com.example.cryptotalk.repository.NotificationConditionRepository;
import com.example.cryptotalk.service.NotificationService;
import com.example.cryptotalk.service.UpbitService;
import com.example.cryptotalk.util.AESUtil;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private final CryptoPriceRepository cryptoPriceRepository;
    private final NotificationConditionRepository conditionRepository;
    private final NotificationService notificationService;
    private final AESUtil aesUtil;

    public NotificationController(CryptoPriceRepository cryptoPriceRepository, NotificationConditionRepository conditionRepository, AESUtil aesUtil, UpbitService upbitService, NotificationService notificationService) {
        this.cryptoPriceRepository = cryptoPriceRepository;
        this.conditionRepository = conditionRepository;
        this.aesUtil = aesUtil;
        this.upbitService = upbitService;
        this.notificationService = notificationService;
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
    public String addNotification(NotificationCondition condition, Authentication authentication, HttpServletRequest request) {

        try {
            // 카카오 OAuth 인증된 사용자 닉네임 가져오기
            String kakaoNickname = "Unknown"; // 기본값 설정
            if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                kakaoNickname = (String) ((Map<String, Object>) oauth2User.getAttribute("properties")).get("nickname");
            }

            // 닉네임 설정
            condition.setKakaoNickname(kakaoNickname);

            // 기타 정보 설정
            condition.setPhoneNumber(aesUtil.encrypt(condition.getPhoneNumber()));
            condition.setActive(true);
            condition.setCreatedAt(LocalDateTime.now());

            // 저장
            conditionRepository.save(condition);
            notificationService.evaluateConditions(condition.getMarket(), cryptoPriceRepository.findByMarket(condition.getMarket()).getPrice(), request);

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/notifications/new?error";
        }

        return "redirect:/notifications/new?success";
    }
}
