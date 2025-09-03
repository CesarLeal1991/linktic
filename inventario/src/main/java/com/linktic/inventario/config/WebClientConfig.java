package com.linktic.inventario.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${productos.service.url}")
    private String productosUrl;

    @Value("${services.http.timeout-millis:3000}")
    private int timeoutMillis;

    @Bean
    public WebClient productosWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(productosUrl)
                .build();
    }
}
