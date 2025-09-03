package com.linktic.inventario.controller;

import com.linktic.inventario.api.JsonApi;
import com.linktic.inventario.model.Inventario;
import com.linktic.inventario.service.InventarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService service;
    private final WebClient productosClient;

    @Value("${productos.api.key}")
    private String apiKey;

    @Value("${productos.api.timeout}")
    private int timeoutMillis;

    public InventarioController(InventarioService service, WebClient productosWebClient) {
        this.service = service;
        this.productosClient = productosWebClient;
    }

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        List<Inventario> inventario = service.getAll();
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<?> getCantidad(@PathVariable Long productoId) {
        Optional<Inventario> inv = service.getByProductoId(productoId);
        if (inv.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(JsonApi.error("Not found", "Inventario para producto " + productoId + " no existe"));
        }
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("productoId", inv.get().getProductoId());
        attrs.put("cantidad", inv.get().getCantidad());
        return ResponseEntity.ok(JsonApi.single("inventario", inv.get().getId(), attrs));
    }

    @PutMapping("/{productoId}")
    public ResponseEntity<?> actualizar(@PathVariable Long productoId, @RequestBody Map<String, Integer> body) {
        Integer cantidad = body.getOrDefault("cantidad", 0);
        Inventario inv = service.updateCantidad(productoId, cantidad);

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("productoId", inv.getProductoId());
        attrs.put("cantidad", inv.getCantidad());

        return ResponseEntity.ok(JsonApi.single("inventario", inv.getId(), attrs));
    }

    @PostMapping("/compra")
    public ResponseEntity<?> comprar(@RequestBody Map<String, Object> body) {
        Long productoId;
        Integer cantidad;

        if (body.containsKey("data")) {
            Map<?, ?> data = (Map<?, ?>) body.get("data");
            Map<?, ?> attrs = (Map<?, ?>) data.get("attributes");
            productoId = Long.valueOf(String.valueOf(attrs.get("productoId")));
            cantidad = Integer.valueOf(String.valueOf(attrs.get("cantidad")));
        } else {
            productoId = Long.valueOf(String.valueOf(body.get("productoId")));
            cantidad = Integer.valueOf(String.valueOf(body.get("cantidad")));
        }

        try {
            Mono<Map> respMono = productosClient.get()
                    .uri("/productos/{id}", productoId)
                    .header("X-API-KEY", apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofMillis(timeoutMillis));

            Map productoResp = respMono.block(Duration.ofMillis(timeoutMillis));
            if (productoResp == null) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(JsonApi.error("Service error", "No response from productos"));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(JsonApi.error("Producto no encontrado", "Producto con id " + productoId + " no existe o servicio inaccesible"));
        }

        try {
            Inventario updated = service.descontar(productoId, cantidad);

            Map<String, Object> bodyResp = new HashMap<>();
            bodyResp.put("productoId", productoId);
            bodyResp.put("cantidadComprada", cantidad);
            bodyResp.put("cantidadRestante", updated.getCantidad());

            return ResponseEntity.ok(JsonApi.single("compra", updated.getId(), bodyResp));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(JsonApi.error("Inventario no encontrado", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(JsonApi.error("Inventario insuficiente", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JsonApi.error("Error", e.getMessage()));
        }
    }
}
