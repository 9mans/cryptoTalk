package com.example.cryptotalk.security;

import com.example.cryptotalk.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil) {
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
        String token = jwtUtil.createToken(userId, nickname);

        logger.info("Successfully authenticated user: " + nickname);
        logger.debug("generated JWT : {} " + token);


        response.setHeader("Authorization", "Bearer " + token);
        response.sendRedirect("/notifications/new");

    }
}
