package com.tuproyecto.tienda_backend.controller;

import com.tuproyecto.tienda_backend.entity.Cliente;
import com.tuproyecto.tienda_backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private ClienteRepository clienteRepository;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credenciales) {
        log.info("Intento de login para usuario: {}", credenciales.get("username"));
        String username = credenciales.get("username");
        String password = credenciales.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Faltan credenciales"));
        }

        return clienteRepository.findByUsername(username)
                .map(cliente -> {
                    if (cliente.getPassword() != null && cliente.getPassword().equals(password)) {
                        log.info("Login exitoso para: {}", username);
                        return ResponseEntity.ok(Map.of(
                                "mensaje", "Login exitoso",
                                "usuario", cliente));
                    } else {
                        log.warn("Contraseña incorrecta para: {}", username);
                        return ResponseEntity.status(401).body(Map.of("mensaje", (Object) "Contraseña incorrecta"));
                    }
                })
                .orElseGet(() -> {
                    log.warn("Usuario no encontrado: {}", username);
                    return ResponseEntity.status(404).body(Map.of("mensaje", "Usuario no encontrado"));
                });
    }

    // REGISTRO
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Cliente nuevoCliente) {
        log.info("Intento de registro para: {}", nuevoCliente.getUsername());

        try {
            // Verificar si el usuario ya existe
            if (clienteRepository.findByUsername(nuevoCliente.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El nombre de usuario ya existe"));
            }

            // Verificar si el email ya existe
            if (nuevoCliente.getEmail() != null && clienteRepository.findByEmail(nuevoCliente.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo electrónico ya está registrado"));
            }

            // Por defecto, todos los nuevos registros son USER
            if (nuevoCliente.getRol() == null) {
                nuevoCliente.setRol("USER");
            }

            Cliente guardado = clienteRepository.save(nuevoCliente);
            log.info("Usuario registrado con ID: {}", guardado.getId());

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Usuario registrado correctamente",
                    "usuario", guardado));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: ", e);
            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", "Error: Datos duplicados o inválidos (posiblemente email o usuario ya existen)"));
        } catch (Exception e) {
            log.error("Error al registrar usuario: ", e);
            return ResponseEntity.status(500).body(Map.of("mensaje", "Error interno al registrar: " + e.getMessage()));
        }
    }
}
