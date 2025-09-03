package com.linktic.inventario.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyFilter implements WebFilter {

    // Usar la propiedad que ya definiste en application.properties
    @Value("${productos.api.key}")
    private String apiKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Validar que la cabecera "X-API-KEY" coincida con la clave esperada
        String requestApiKey = exchange.getRequest()
                .getHeaders()
                .getFirst("X-API-KEY");

        if (apiKey.equals(requestApiKey)) {
            // Si es correcta, continuar con la petición
            return chain.filter(exchange);
        } else {
            // Si no es correcta, devolver 401 Unauthorized
            exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
