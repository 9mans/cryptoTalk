package com.example.cryptotalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoTalkApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoTalkApplication.class, args);
    }

}
