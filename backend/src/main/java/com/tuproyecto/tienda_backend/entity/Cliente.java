package com.tuproyecto.tienda_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "clientes")
@Data // Lombok genera getters, setters y toString
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String direccion;

    private String username;
    private String password;
    private String rol; // 'ADMIN' o 'USER'

}