package com.example.cryptotalk.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.client.RestTemplate;

public class KakaoLogoutHandler implements LogoutHandler {

    private static final Logger log = LoggerFactory.getLogger(KakaoLogoutHandler.class);

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("=== KakaoLogoutHandler: 로그아웃 시작 ===");

        // 세션에서 액세스 토큰 가져오기
        String accessToken = (String) request.getSession().getAttribute("accessToken");
        if (accessToken != null) {
            log.info("세션에서 액세스 토큰 확인: {}", accessToken);

            try {
                // 1. 카카오 API를 호출해 토큰 무효화
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + accessToken);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                restTemplate.exchange("https://kapi.kakao.com/v1/user/logout", HttpMethod.POST, entity, String.class);
                log.info("카카오 로그아웃 API 호출 성공: 토큰 무효화 완료");

                // 2. 세션 무효화
                request.getSession().invalidate();
                log.info("세션 무효화 완료");
            } catch (Exception e) {
                log.error("카카오 로그아웃 중 오류 발생", e);
            }
        } else {
            log.warn("세션에 액세스 토큰이 없습니다. 이미 로그아웃된 상태일 수 있습니다.");
        }

        try {
            // 3. 카카오 로그아웃 URL로 리다이렉트
            String logoutUrl = "https://kauth.kakao.com/oauth/logout"
                    + "?client_id=083b230153788952d48383ed1edcf65e"
                    + "&logout_redirect_uri=http://localhost:8080/notification/new";
            log.info("로그아웃 리다이렉트 URL: {}", logoutUrl);
            response.sendRedirect(logoutUrl);
            log.info("리다이렉트 성공");
        } catch (Exception e) {
            log.error("로그아웃 리다이렉트 중 오류 발생", e);
        }

        log.info("=== KakaoLogoutHandler: 로그아웃 완료 ===");
    }
}