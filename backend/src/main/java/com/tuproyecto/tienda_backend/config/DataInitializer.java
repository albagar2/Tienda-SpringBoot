package com.tuproyecto.tienda_backend.config;

import com.tuproyecto.tienda_backend.entity.Cliente;
import com.tuproyecto.tienda_backend.repository.ClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ClienteRepository repository,
            com.tuproyecto.tienda_backend.repository.ProductoRepository productoRepository,
            com.tuproyecto.tienda_backend.repository.PedidoRepository pedidoRepository) {
        return args -> {
            Cliente admin = createOrUpdateUser(repository, "Administrador", "admin", "admin123", "admin@tienda.com",
                    "Calle Falsa 123", "ADMIN");
            Cliente user = createOrUpdateUser(repository, "Usuario Normal", "user", "user123", "user@tienda.com",
                    "Avenida Siempre Viva 742", "USER");
            Cliente ana = createOrUpdateUser(repository, "Ana García", "ana", "ana123", "ana@gmail.com",
                    "Calle Luna 45", "USER");
            Cliente juan = createOrUpdateUser(repository, "Juan Pérez", "juan", "juan123", "juan@hotmail.com",
                    "Avenida Sol 99", "USER");

            if (productoRepository.count() == 0) {
                seedProducts(productoRepository);
            }

            // SEED PEDIDOS (Orders) if empty
            if (pedidoRepository.count() == 0 && user != null && productoRepository.count() > 0) {
                seedSampleOrders(pedidoRepository, user, ana, juan, productoRepository);
            }
        };
    }

    private Cliente createOrUpdateUser(ClienteRepository repo, String nombre, String username, String pass,
            String email, String dir, String rol) {
        Cliente user = repo.findByUsername(username).orElse(null);
        if (user == null)
            user = repo.findByEmail(email).orElse(null);
        if (user == null)
            user = new Cliente();

        user.setNombre(nombre);
        user.setUsername(username);
        user.setPassword(pass);
        user.setEmail(email);
        user.setDireccion(dir);
        user.setRol(rol);
        System.out.println("Usuario " + username.toUpperCase() + " actualizado/creado");
        return repo.save(user);
    }

    private void seedProducts(com.tuproyecto.tienda_backend.repository.ProductoRepository repo) {
        repo.save(createProduct("Laptop Gamer", 1200.0, 10, "Potente laptop para juegos", "laptop.png"));
        repo.save(createProduct("Smartphone Pro", 800.0, 20, "Teléfono de última generación", "phone.png"));
        repo.save(createProduct("Auriculares Bluetooth", 50.0, 50, "Sonido de alta fidelidad", "headphones.png"));
        System.out.println("Productos de prueba creados.");
    }

    private com.tuproyecto.tienda_backend.entity.Producto createProduct(String nom, Double pre, Integer stock,
            String desc, String img) {
        com.tuproyecto.tienda_backend.entity.Producto p = new com.tuproyecto.tienda_backend.entity.Producto();
        p.setNombre(nom);
        p.setPrecio(pre);
        p.setStock(stock);
        p.setDescripcion(desc);
        // Assuming Producto entity has an imagenUrl field and setter
        p.setImagenUrl(img);
        return p;
    }

    private void seedSampleOrders(com.tuproyecto.tienda_backend.repository.PedidoRepository repo, Cliente u, Cliente a,
            Cliente j, com.tuproyecto.tienda_backend.repository.ProductoRepository pRepo) {
        com.tuproyecto.tienda_backend.entity.Producto p1 = pRepo.findAll().get(0);

        // Pedido General
        createOrder(repo, u, p1, 1, "PENDIENTE", 0);
        // Pedido Ana
        createOrder(repo, a, p1, 1, "ENTREGADO", 2);
        // Pedido Juan
        createOrder(repo, j, p1, 2, "PENDIENTE", 0);

        System.out.println("Pedidos de prueba creados.");
    }

    private void createOrder(com.tuproyecto.tienda_backend.repository.PedidoRepository repo, Cliente c,
            com.tuproyecto.tienda_backend.entity.Producto p, int cant, String estado, int daysAgo) {
        if (c == null)
            return;
        com.tuproyecto.tienda_backend.entity.Pedido pedido = new com.tuproyecto.tienda_backend.entity.Pedido();
        pedido.setCliente(c);
        pedido.setFecha(java.time.LocalDateTime.now().minusDays(daysAgo));
        pedido.setEstado(estado);
        pedido.setTotal(p.getPrecio() * cant);

        com.tuproyecto.tienda_backend.entity.DetallePedido detalle = new com.tuproyecto.tienda_backend.entity.DetallePedido();
        detalle.setProducto(p);
        detalle.setCantidad(cant);
        detalle.setPrecioUnitario(p.getPrecio());
        detalle.setSubtotal(p.getPrecio() * cant);
        detalle.setPedido(pedido);

        pedido.setDetalles(java.util.List.of(detalle));
        repo.save(pedido);
    }
}
