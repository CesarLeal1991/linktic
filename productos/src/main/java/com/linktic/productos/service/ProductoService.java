package com.linktic.productos.service;

import com.linktic.productos.model.Producto;
import com.linktic.productos.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public Optional<Producto> getById(Long id) {
        return repository.findById(id);
    }

    public List<Producto> getAll() {
        return repository.findAll();
    }

    public Producto save(Producto producto) {
        return repository.save(producto);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
