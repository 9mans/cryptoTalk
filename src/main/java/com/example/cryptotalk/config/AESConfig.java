package com.example.cryptotalk.config;

import com.example.cryptotalk.util.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AESConfig {

    @Value("${encryption.key}")
    private String encryptionKey;

    @Bean
    public AESUtil aesUtil() {
        return new AESUtil(encryptionKey);
    }
}
