package com.tuproyecto.tienda_backend.controller;

import com.tuproyecto.tienda_backend.entity.Producto;
import com.tuproyecto.tienda_backend.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @PutMapping("/{id}/stock")
    public Producto updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setStock(body.get("stock"));
        return productoRepository.save(producto);
    }

    // --- NUEVOS ENDPOINTS CRUD PARA ADMIN ---

    // CREAR PRODUCTO
    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        // IMPORTANTE: Forzar el ID a null para que JPA sepa que es NUEVO y use
        // AUTO_INCREMENT
        // Si viene 0, JPA intenta hacer update y falla.
        producto.setId(null);
        return productoRepository.save(producto);
    }

    // ACTUALIZAR PRODUCTO COMPLETO
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto detalles) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(detalles.getNombre());
        producto.setDescripcion(detalles.getDescripcion());
        producto.setPrecio(detalles.getPrecio());
        producto.setStock(detalles.getStock());
        producto.setImagenUrl(detalles.getImagenUrl());

        return productoRepository.save(producto);
    }

    // ELIMINAR PRODUCTO
    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
    }
}