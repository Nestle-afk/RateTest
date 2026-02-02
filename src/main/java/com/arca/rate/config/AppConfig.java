package com.arca.rate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    private final String EXCHANGE_API = "https://www.alphavantage.co/query";

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(EXCHANGE_API)
                .build();
    }
}
