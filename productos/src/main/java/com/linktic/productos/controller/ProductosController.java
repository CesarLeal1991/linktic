package com.linktic.productos.controller;

import com.linktic.productos.api.JsonApi;
import com.linktic.productos.model.Producto;
import com.linktic.productos.service.ProductoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
public class ProductosController {

    private final ProductoService service;
    private final String apiKey;

    // Constructor único para inyección de dependencias
    public ProductosController(ProductoService service, @Value("${services.api-key}") String apiKey) {
        this.service = service;
        this.apiKey = apiKey;
    }

    // Validación simple de API key
    private boolean validarApiKey(String key) {
        return key != null && key.equals(apiKey);
    }

    // Crear producto
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestHeader(value = "X-API-KEY", required = false) String key,
                                           @RequestBody Map<String, Object> body) {

        if (!validarApiKey(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JsonApi.error("Unauthorized", "API key inválida"));
        }

        String nombre = (String) body.get("nombre");
        Double precio = Double.valueOf(String.valueOf(body.get("precio")));
        String descripcion = body.getOrDefault("descripcion", "").toString();

        Producto p = new Producto(nombre, precio, descripcion);
        Producto saved = service.save(p);

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("nombre", saved.getNombre());
        attrs.put("precio", saved.getPrecio());
        attrs.put("descripcion", saved.getDescripcion());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JsonApi.single("producto", saved.getId(), attrs));
    }

    // Listar todos los productos
    @GetMapping
    public ResponseEntity<?> getAllProductos(@RequestHeader(value = "X-API-KEY", required = false) String key) {

        if (!validarApiKey(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JsonApi.error("Unauthorized", "API key inválida"));
        }

        List<Map<String, Object>> productos = service.getAll().stream()
                .map(p -> {
                    Map<String, Object> attrs = new HashMap<>();
                    attrs.put("nombre", p.getNombre());
                    attrs.put("precio", p.getPrecio());
                    attrs.put("descripcion", p.getDescripcion());
                    return attrs;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(JsonApi.collection("productos", productos));
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProducto(@PathVariable Long id,
                                         @RequestHeader(value = "X-API-KEY", required = false) String key) {

        if (!validarApiKey(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JsonApi.error("Unauthorized", "API key inválida"));
        }

        return service.getById(id)
                .map(p -> {
                    Map<String, Object> attrs = new HashMap<>();
                    attrs.put("nombre", p.getNombre());
                    attrs.put("precio", p.getPrecio());
                    attrs.put("descripcion", p.getDescripcion());

                    return ResponseEntity.ok(JsonApi.single("producto", p.getId(), attrs));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(JsonApi.error("Not found", "Producto con id " + id + " no existe")));
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id,
                                                @RequestHeader(value = "X-API-KEY", required = false) String key,
                                                @RequestBody Map<String, Object> body) {

        if (!validarApiKey(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JsonApi.error("Unauthorized", "API key inválida"));
        }

        return service.getById(id)
                .map(p -> {
                    String nombre = body.getOrDefault("nombre", p.getNombre()).toString();
                    Double precio = body.containsKey("precio") ? Double.valueOf(String.valueOf(body.get("precio"))) : p.getPrecio();
                    String descripcion = body.getOrDefault("descripcion", p.getDescripcion()).toString();

                    p.setNombre(nombre);
                    p.setPrecio(precio);
                    p.setDescripcion(descripcion);

                    Producto actualizado = service.save(p);

                    Map<String, Object> attrs = new HashMap<>();
                    attrs.put("nombre", actualizado.getNombre());
                    attrs.put("precio", actualizado.getPrecio());
                    attrs.put("descripcion", actualizado.getDescripcion());

                    return ResponseEntity.ok(JsonApi.single("producto", actualizado.getId(), attrs));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(JsonApi.error("Not found", "Producto con id " + id + " no existe")));
    }

    // Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id,
                                              @RequestHeader(value = "X-API-KEY", required = false) String key) {

        if (!validarApiKey(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(JsonApi.error("Unauthorized", "API key inválida"));
        }

        return service.getById(id)
                .map(p -> {
                    service.delete(id);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(JsonApi.error("Not found", "Producto con id " + id + " no existe")));
    }
}
