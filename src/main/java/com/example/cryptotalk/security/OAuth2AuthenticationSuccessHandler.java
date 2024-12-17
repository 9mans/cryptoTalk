package com.example.cryptotalk.security;

import com.example.cryptotalk.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private final OAuth2AuthorizedClientService clientService;
    private final JwtUtil jwtUtil;

    public OAuth2AuthenticationSuccessHandler(OAuth2AuthorizedClientService clientService,JwtUtil jwtUtil) {
        this.clientService = clientService;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        String userId = oAuth2User.getName();
        String nickname = null;

        Map<String, Object> attributes = (Map<String, Object>) oAuth2User.getAttributes();

        if (attributes.containsKey("properties")) {
            Object propertiesObj = attributes.get("properties");
            if (propertiesObj instanceof Map<?, ?> properties) {
                Object nicknameObj = properties.get("nickname");
                if (nicknameObj instanceof String) {
                    nickname = (String) nicknameObj;
                }
            }
        }

        if (nickname == null) {
            nickname = "Anonymous";
        }

        // Extract accessToken from OAuth2AuthorizedClient
        OAuth2AuthorizedClient authorizedClient = clientService.loadAuthorizedClient(
                "kakao", authentication.getName());
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        // Store accessToken in session
        request.getSession().setAttribute("accessToken", accessToken);
        String token = jwtUtil.createToken(userId, nickname);

        logger.info("Successfully authenticated user: " + nickname);
        logger.debug("generated JWT : {} " + token);
        logger.debug("Kakao AccessToken: {}", accessToken);

        response.setHeader("Authorization", "Bearer " + token);
        response.sendRedirect("/notifications/new");

    }
}
