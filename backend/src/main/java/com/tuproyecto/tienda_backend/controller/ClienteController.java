package com.tuproyecto.tienda_backend.controller;

import com.tuproyecto.tienda_backend.entity.Cliente;
import com.tuproyecto.tienda_backend.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // 1. Obtener todos los clientes (GET /api/clientes)
    // Ideal para listados de administración
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // 2. Obtener un cliente por su ID (GET /api/clientes/{id})
    // Ideal para ver el perfil de un usuario
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerCliente(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);

        // Si existe, devolvemos OK (200) y el cliente. Si no, Not Found (404)
        if (cliente.isPresent()) {
            return new ResponseEntity<>(cliente.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 3. Crear un nuevo cliente (POST /api/clientes)
    // Recibe un JSON, lo guarda y devuelve el cliente guardado con su nuevo ID
    @PostMapping
    public ResponseEntity<Cliente> guardarCliente(@RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteRepository.save(cliente);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

    // 4. Eliminar cliente (DELETE /api/clientes/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}