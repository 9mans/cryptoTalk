package com.example.cryptotalk.config;

import com.example.cryptotalk.security.KakaoLogoutHandler;
import com.example.cryptotalk.security.OAuth2AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    public SecurityConfig(OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) {
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/notifications/new", "/login/kakao", "/login/oauth2/code/kakao", "/oauth2/authorization/kakao", "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .loginPage("/login/kakao")
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureUrl("/login?error=true"))
                .logout(logout -> logout.logoutUrl("/logout")
                        .addLogoutHandler(new KakaoLogoutHandler())
                        .deleteCookies("Authorization", "JSESSIONID")
                        .invalidateHttpSession(true)).csrf(csrf -> csrf.disable())
                .requestCache(requestCache -> requestCache.disable());

        return httpSecurity.build();
    }
}
