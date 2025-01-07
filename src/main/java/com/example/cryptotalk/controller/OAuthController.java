package com.example.cryptotalk.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class OAuthController {

    @GetMapping("/login/kakao")
    public String kakaoLogin() {
        return "redirect:/oauth2/authorization/kakao";
    }

    @GetMapping("/login/oauth2/code/kakao")
    public String kakaoCallback(@RegisteredOAuth2AuthorizedClient("kakao") OAuth2AuthorizedClient oAuth2AuthorizedClient, OAuth2User oAuth2User, Model model) {

        String accessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();

        Map<String, Object> properties = (Map<String, Object>) oAuth2User.getAttribute("properties");
        String nickname = properties != null ? (String) properties.get("nickname") : "Unknown";

        model.addAttribute("nickname", nickname);
        model.addAttribute("accessToken", accessToken);


        System.out.println("accessToken" + accessToken);
        System.out.println("nickname" + nickname);

        return "redirect:/notifications/new";
    }
}
