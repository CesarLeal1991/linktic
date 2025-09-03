package com.linktic.inventario.controller;

import com.linktic.inventario.model.Inventario;
import com.linktic.inventario.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InventarioControllerTest {

    private InventarioService inventarioService;
    private WebClient webClient;
    private InventarioController inventarioController;

    @BeforeEach
    void setUp() {
        inventarioService = mock(InventarioService.class);
        webClient = mock(WebClient.class);

        inventarioController = new InventarioController(inventarioService, webClient, "test-api-key");

        // Mock de WebClient
        WebClient.RequestHeadersUriSpec uriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpecMock);
        when(uriSpecMock.uri("/productos/{id}", 1L)).thenReturn(headersSpecMock);
        when(headersSpecMock.header(eq("X-API-KEY"), anyString())).thenReturn(headersSpecMock);
        when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Map.class)).thenReturn(Mono.just(new HashMap<>()));
    }

    @Test
    void testComprar() {
        Long productoId = 1L;
        int cantidad = 2;

        // Mock del service
        Inventario inventarioMock = new Inventario();
        inventarioMock.setId(1L);
        inventarioMock.setProductoId(productoId);
        inventarioMock.setCantidad(10);

        when(inventarioService.descontar(eq(productoId), eq(cantidad))).thenReturn(inventarioMock);

        Map<String, Object> body = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("productoId", productoId);
        attributes.put("cantidad", cantidad);
        data.put("attributes", attributes);
        body.put("data", data);

        ResponseEntity<?> response = inventarioController.comprar(body);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testListarTodos() {
        Inventario inv1 = new Inventario();
        inv1.setId(1L);
        inv1.setProductoId(1L);
        inv1.setCantidad(10);

        Inventario inv2 = new Inventario();
        inv2.setId(2L);
        inv2.setProductoId(2L);
        inv2.setCantidad(5);

        when(inventarioService.getAll()).thenReturn(java.util.List.of(inv1, inv2));

        ResponseEntity<?> response = inventarioController.listarTodos();
        assertEquals(200, response.getStatusCodeValue());
    }
}
