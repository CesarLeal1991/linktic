package com.linktic.inventario.service;

import com.linktic.inventario.model.Inventario;
import com.linktic.inventario.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    private final InventarioRepository repository;

    public InventarioService(InventarioRepository repository) {
        this.repository = repository;
    }

    public List<Inventario> getAll() {
        return repository.findAll();
    }

    public Optional<Inventario> getByProductoId(Long productoId) {
        return repository.findByProductoId(productoId);
    }

    @Transactional
    public Inventario updateCantidad(Long productoId, Integer cantidad) {
        Inventario inv = repository.findByProductoId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en inventario"));
        inv.setCantidad(cantidad);
        return repository.save(inv);
    }

    @Transactional
    public Inventario descontar(Long productoId, Integer cantidad) {
        Inventario inv = repository.findByProductoId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en inventario"));

        if (inv.getCantidad() < cantidad) {
            throw new IllegalStateException("Cantidad insuficiente en inventario");
        }

        inv.setCantidad(inv.getCantidad() - cantidad);
        return repository.save(inv);
    }
}
