package com.tuproyecto.tienda_backend.repository;

import com.tuproyecto.tienda_backend.entity.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsername(String username);

    Optional<Cliente> findByEmail(String email);
}
