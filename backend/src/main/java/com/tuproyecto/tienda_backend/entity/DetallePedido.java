package com.tuproyecto.tienda_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString; // Necesario para el Exclude
import com.fasterxml.jackson.annotation.JsonIgnore; // Necesario para cortar el bucle

@Entity
@Table(name = "detalles_pedido")
@Data
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    // Relación con Producto (Un detalle tiene 1 producto)
    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // Relación con Pedido (Muchos detalles pertenecen a 1 pedido)
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @com.fasterxml.jackson.annotation.JsonBackReference // <--- Mejor que JsonIgnore para relaciones bidireccionales
    @ToString.Exclude // <--- 2. CORTA EL BUCLE DE LOMBOK (Vital)
    private Pedido pedido;
}