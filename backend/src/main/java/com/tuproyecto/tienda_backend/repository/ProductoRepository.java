package com.tuproyecto.tienda_backend.repository;
import com.tuproyecto.tienda_backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductoRepository extends JpaRepository<Producto, Long> {}