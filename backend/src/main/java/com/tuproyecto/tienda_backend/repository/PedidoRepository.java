package com.tuproyecto.tienda_backend.repository;

import com.tuproyecto.tienda_backend.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteId(Long clienteId);

    // Sort by date descending
    List<Pedido> findAllByOrderByFechaDesc();

    List<Pedido> findByClienteIdOrderByFechaDesc(Long clienteId);

    List<Pedido> findByEstadoNotOrderByFechaDesc(String estado);
}