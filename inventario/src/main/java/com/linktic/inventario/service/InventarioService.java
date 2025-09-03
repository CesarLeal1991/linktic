package com.linktic.inventario.service;

import com.linktic.inventario.model.Inventario;
import com.linktic.inventario.repository.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    private final InventarioRepository repo;

    public InventarioService(InventarioRepository repo) {
        this.repo = repo;
    }

    public Optional<Inventario> getByProductoId(Long productoId) {
        return repo.findByProductoId(productoId);
    }

    public List<Inventario> getAll() {
        return repo.findAll();
    }

    @Transactional
    public Inventario updateCantidad(Long productoId, int nuevaCantidad) {
        Inventario inv = repo.findByProductoId(productoId).orElseGet(() -> {
            Inventario i = new Inventario(productoId, 0);
            return i;
        });
        inv.setCantidad(nuevaCantidad);
        return repo.save(inv);
    }

    @Transactional
    public Inventario descontar(Long productoId, int cantidad) {
        Inventario inv = repo.findByProductoId(productoId).orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado"));
        if (inv.getCantidad() < cantidad) throw new IllegalStateException("Inventario insuficiente");
        inv.setCantidad(inv.getCantidad() - cantidad);
        return repo.save(inv);
    }
}
