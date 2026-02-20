package com.tuproyecto.tienda_backend.controller;

import com.tuproyecto.tienda_backend.entity.Pedido;
import com.tuproyecto.tienda_backend.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import com.tuproyecto.tienda_backend.entity.Producto;
import com.tuproyecto.tienda_backend.repository.ProductoRepository;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private com.tuproyecto.tienda_backend.repository.ClienteRepository clienteRepository;

    // 1. OBTENER TODOS LOS PEDIDOS (GET) - O FILTRAR POR CLIENTE
    @GetMapping
    public List<Pedido> listarPedidos(@RequestParam(required = false) Long clienteId) {
        if (clienteId != null) {
            return pedidoRepository.findByClienteIdOrderByFechaDesc(clienteId);
        }
        // El administrador ve todo EXCEPTO los cancelados para no ensuciar la lista
        return pedidoRepository.findByEstadoNotOrderByFechaDesc("CANCELADO");
    }

    // 1.5 ELIMINAR PEDIDO (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarPedido(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        Map<String, String> respuesta = Collections.singletonMap("mensaje", "Pedido eliminado correctamente");
        return ResponseEntity.ok(respuesta);
    }

    // 2. GUARDAR PEDIDO (POST) - ¡AQUÍ ESTÁ LA MAGIA!
    // 2. GUARDAR PEDIDO (POST) - ¡AQUÍ ESTÁ LA MAGIA!
    @PostMapping
    public ResponseEntity<Object> guardarPedido(@RequestBody Pedido pedido) {

        // A) Poner la fecha actual si no viene
        if (pedido.getFecha() == null) {
            pedido.setFecha(LocalDateTime.now());
        }

        // A.1) ASIGNAR EL CLIENTE REAL DE LA BASE DE DATOS
        if (pedido.getCliente() != null && pedido.getCliente().getId() != null) {
            com.tuproyecto.tienda_backend.entity.Cliente clienteDb = clienteRepository
                    .findById(pedido.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            pedido.setCliente(clienteDb);
        } else {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("mensaje", "Error: El pedido debe tener un cliente asignado."));
        }

        // B) VINCULACIÓN IMPORTANTE:
        if (pedido.getDetalles() != null) {
            // VALIDACIÓN Y ACTUALIZACIÓN DE STOCK
            for (var detalle : pedido.getDetalles()) {
                Producto producto = productoRepository.findById(detalle.getProducto().getId())
                        .orElseThrow(
                                () -> new RuntimeException("Producto no encontrado: " + detalle.getProducto().getId()));

                if (producto.getStock() < detalle.getCantidad()) {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("mensaje",
                            "No hay suficiente stock para: " + producto.getNombre()));
                }

                // Restar stock
                producto.setStock(producto.getStock() - detalle.getCantidad());
                productoRepository.save(producto);

                // IMPORTANTE: Asignar el producto gestionado (managed) al detalle
                detalle.setProducto(producto);

                // Vincular detalle al pedido
                detalle.setPedido(pedido);
            }
        }

        // C) Guardar en Base de Datos
        Pedido guardado = pedidoRepository.save(pedido);

        Map<String, Object> respuesta = Map.of(
                "mensaje", "Pedido guardado correctamente",
                "pedido", guardado);
        return ResponseEntity.ok(respuesta);
    }

    // 3. ACTUALIZAR ESTADO (PUT) - SOLO ADMIN
    @PutMapping("/{id}/estado")
    public ResponseEntity<Object> updateEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // BLOQUEO: Si ya está cancelado, el admin no puede "revivirlo" o cambiarlo por
        // error
        if ("CANCELADO".equals(pedido.getEstado())) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("mensaje",
                            "No se puede cambiar el estado de un pedido ya CANCELADO."));
        }

        pedido.setEstado(body.get("estado"));
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    // 4. CANCELAR PEDIDO (POST) - USUARIO (CON RECUPERACIÓN DE STOCK)
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Object> cancelarPedido(@PathVariable Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if (!"PENDIENTE".equals(pedido.getEstado())) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("mensaje", "Solo se pueden cancelar pedidos PENDIENTES."));
        }

        // Recuperar Stock
        if (pedido.getDetalles() != null) {
            for (var detalle : pedido.getDetalles()) {
                Producto producto = detalle.getProducto();
                if (producto != null) {
                    producto.setStock(producto.getStock() + detalle.getCantidad());
                    productoRepository.save(producto);
                }
            }
        }

        pedido.setEstado("CANCELADO");
        Pedido guardado = pedidoRepository.save(pedido);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido cancelado y stock recuperado",
                "pedido", guardado));
    }
}