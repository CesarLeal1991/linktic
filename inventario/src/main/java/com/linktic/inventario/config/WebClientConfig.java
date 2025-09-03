package com.linktic.inventario.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${PRODUCTOS_SERVICE_URL:http://productos:8080}")
    private String productosUrl;

    @Bean
    public WebClient productosWebClient() {
        return WebClient.builder()
                .baseUrl(productosUrl)
                .build();
    }
}
