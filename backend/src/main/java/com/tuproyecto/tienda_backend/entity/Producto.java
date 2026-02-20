package com.tuproyecto.tienda_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "productos")
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    private Double precio;
    private Integer stock;

    @Column(length = 500)
    private String imagenUrl;
}