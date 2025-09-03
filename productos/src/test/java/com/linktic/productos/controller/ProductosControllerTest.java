package com.linktic.productos.controller;

import com.linktic.productos.model.Producto;
import com.linktic.productos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductosControllerTest {

    private ProductoService service;
    private ProductosController controller;
    private final String API_KEY = "123456789";

    @BeforeEach
    void setup() {
        service = mock(ProductoService.class);
        controller = new ProductosController(service, API_KEY);
    }

    @Test
    void testGetAllProductos() {
        Producto p1 = new Producto("Producto1", 100.0, "Desc1");
        p1.setId(1L);
        when(service.getAll()).thenReturn(Arrays.asList(p1));

        ResponseEntity<?> response = controller.getAllProductos(API_KEY);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetProductoByIdFound() {
        Producto p = new Producto("Producto1", 100.0, "Desc1");
        p.setId(1L);

        when(service.getById(1L)).thenReturn(Optional.of(p));

        ResponseEntity<?> response = controller.getProducto(1L, API_KEY);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetProductoByIdNotFound() {
        when(service.getById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getProducto(1L, API_KEY);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testCrearProducto() {
        Producto p = new Producto("Producto1", 100.0, "Desc1");
        p.setId(1L);

        when(service.save(any(Producto.class))).thenReturn(p);

        ResponseEntity<?> response = controller.crearProducto(API_KEY,
                Map.of("nombre", "Producto1", "precio", 100.0, "descripcion", "Desc1"));

        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    void testActualizarProducto() {
        Producto p = new Producto("Producto1", 100.0, "Desc1");
        p.setId(1L);
        when(service.getById(1L)).thenReturn(Optional.of(p));
        when(service.save(any(Producto.class))).thenReturn(p);

        ResponseEntity<?> response = controller.actualizarProducto(1L, API_KEY,
                Map.of("nombre", "Producto1 Modificado", "precio", 120.0));

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testEliminarProducto() {
        Producto p = new Producto("Producto1", 100.0, "Desc1");
        p.setId(1L);
        when(service.getById(1L)).thenReturn(Optional.of(p));
        doNothing().when(service).delete(1L);

        ResponseEntity<?> response = controller.eliminarProducto(1L, API_KEY);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testInvalidApiKey() {
        ResponseEntity<?> response = controller.getAllProductos("wrong-key");
        assertEquals(401, response.getStatusCodeValue());
    }
}
