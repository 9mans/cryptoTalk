package com.example.cryptotalk.service;

import com.example.cryptotalk.entity.NotificationCondition;
import com.example.cryptotalk.repository.NotificationConditionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationConditionRepository conditionRepository;
    private final String KAKAO_API_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private final HttpSession httpSession;

    public NotificationService(NotificationConditionRepository conditionRepository, HttpSession httpSession) {
        this.conditionRepository = conditionRepository;
        this.httpSession = httpSession;
    }

    public void evaluateConditions(String market, BigDecimal currentPrice, HttpServletRequest request) {

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
                sendNotification(condition, currentPrice, request);
                condition.setActive(false);
                conditionRepository.save(condition);
            }
        }
    }

    private void sendNotification(NotificationCondition condition, BigDecimal currentPrice, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            String accessToken = (String) session.getAttribute("accessToken");
            if (accessToken == null || accessToken.isEmpty()) {
                throw new IllegalStateException("Access token is missing from session.");
            }

            String templateObject = String.format("""
        {
            "object_type": "text",
            "text": "📈 알림 조건 충족\\n마켓: %s\\n조건: %s\\n목표가: %s\\n현재가: %s",
            "link": {
                "web_url": "https://your-app-url.com/market/%s",
                "mobile_web_url": "https://your-app-url.com/market/%s"
            },
            "button_title": "상세 보기"
        }
        """, condition.getMarket(), condition.getDirection(), condition.getTargetPrice(), currentPrice, condition.getMarket(), condition.getMarket());

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("template_object", templateObject);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.postForObject("https://kapi.kakao.com/v2/api/talk/memo/default/send", requestEntity, String.class);

            System.out.println("알림 전송 성공: " + response);
        } catch (Exception e) {
            System.err.println("알림 전송 중 오류 발생: " + e.getMessage());
        }
    }
}
