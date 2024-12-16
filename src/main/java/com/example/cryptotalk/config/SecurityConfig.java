package com.example.cryptotalk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login/kakao", "/login/oauth2/code/kakao", "/css/**", "/js/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .loginPage("/login/kakao")
                        .defaultSuccessUrl("/notifications/new", true)
                        .failureUrl("/login?error=true"))
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)).csrf(csrf -> csrf.disable())
                .requestCache(requestCache -> requestCache.disable());

        return httpSecurity.build();
    }
}
