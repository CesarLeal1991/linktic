package com.linktic.inventario.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class ApiKeyFilter implements Filter {

    @Value("${services.api-key}")
    private String expected;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) request;
        String apiKey = r.getHeader("X-API-KEY");
        // permitir endpoints públicos si lo deseas (e.g., health). Aquí protegemos todo.
        if (expected != null && expected.equals(apiKey)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"errors\":[{\"title\":\"Unauthorized\",\"detail\":\"Missing or invalid API key\"}]}");
        }
    }
}
