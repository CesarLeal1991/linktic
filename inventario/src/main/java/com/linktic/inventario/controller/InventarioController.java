package com.linktic.inventario.controller;

import com.linktic.inventario.api.JsonApi;
import com.linktic.inventario.model.Inventario;
import com.linktic.inventario.service.InventarioService;
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
    private final String apiKey;
    private final int timeoutMillis;

    // Constructor principal
    public InventarioController(InventarioService service, WebClient productosClient,
                                String apiKey, int timeoutMillis) {
        this.service = service;
        this.productosClient = productosClient;
        this.apiKey = apiKey;
        this.timeoutMillis = timeoutMillis;
    }

    // Constructor simple con timeout por defecto
    public InventarioController(InventarioService service, WebClient productosClient, String apiKey) {
        this(service, productosClient, apiKey, 3000);
    }

    // Listar todos los inventarios
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        List<Inventario> inventario = service.getAll();
        return ResponseEntity.ok(inventario);
    }

    // Obtener cantidad por producto
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

    // Actualizar cantidad
    @PutMapping("/{productoId}")
    public ResponseEntity<?> actualizar(@PathVariable Long productoId, @RequestBody Map<String, Integer> body) {
        Integer cantidad = body.getOrDefault("cantidad", 0);
        Inventario inv = service.updateCantidad(productoId, cantidad);

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("productoId", inv.getProductoId());
        attrs.put("cantidad", inv.getCantidad());

        return ResponseEntity.ok(JsonApi.single("inventario", inv.getId(), attrs));
    }

    // Comprar
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

        // 1) Validar producto existe en Productos service
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

        // 2) Verificar inventario y actualizar
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
