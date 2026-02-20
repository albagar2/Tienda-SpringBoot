CREATE DATABASE tienda_db;
-- --------------------------------------------------------
-- 1. LIMPIEZA INICIAL (Para poder reiniciar pruebas)
-- --------------------------------------------------------
DROP TABLE IF EXISTS detalles_pedido;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS clientes;

-- --------------------------------------------------------
-- 2. CREACIÓN DE TABLAS
-- --------------------------------------------------------

-- Tabla 1: CLIENTES
CREATE TABLE clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    direccion VARCHAR(200)
);

-- Tabla 2: PRODUCTOS
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10, 2) NOT NULL, -- DECIMAL es mejor para dinero
    stock INT NOT NULL,
    imagen_url VARCHAR(255) -- Útil para mostrar fotos en Angular
);

-- Tabla 3: PEDIDOS (Cabecera)
CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL, -- Ej: PENDIENTE, ENVIADO, ENTREGADO
    total DECIMAL(10, 2),
    cliente_id BIGINT,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- Tabla 4: DETALLES_PEDIDO (Relación N:M desglosada)
CREATE TABLE detalles_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL, -- Precio al momento de la compra
    subtotal DECIMAL(10, 2) NOT NULL,
    pedido_id BIGINT,
    producto_id BIGINT,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- --------------------------------------------------------
-- 3. INSERCIÓN DE DATOS FICTICIOS (SEED DATA)
-- --------------------------------------------------------

-- Insertar Clientes
INSERT INTO clientes (nombre, email, direccion) VALUES 
('Ana García', 'ana@gmail.com', 'Calle Falsa 123, Madrid'),
('Carlos López', 'carlos@hotmail.com', 'Av. Libertad 45, Barcelona'),
('Lucía Mendez', 'lucia@empresa.com', 'Plaza Mayor 8, Sevilla');

-- Insertar Productos (Tecnología)
INSERT INTO productos (nombre, descripcion, precio, stock, imagen_url) VALUES 
('Laptop Gamer X1', 'Portátil de alto rendimiento con 16GB RAM', 1200.50, 10, 'assets/img/laptop.jpg'),
('Ratón Inalámbrico', 'Ratón ergonómico con batería de larga duración', 25.99, 50, 'assets/img/mouse.jpg'),
('Monitor 4K', 'Monitor de 27 pulgadas resolución UHD', 350.00, 15, 'assets/img/monitor.jpg'),
('Teclado Mecánico', 'Teclado RGB con switches azules', 89.90, 30, 'assets/img/keyboard.jpg'),
('Auriculares Noise Cancel', 'Auriculares con cancelación de ruido activa', 199.99, 20, 'assets/img/headphones.jpg');

-- Insertar Pedidos
-- Pedido 1: Ana compra un Laptop y un Ratón (Estado: ENVIADO)
INSERT INTO pedidos (fecha, estado, total, cliente_id) VALUES ('2023-10-01 10:30:00', 'ENVIADO', 1226.49, 1);

-- Pedido 2: Carlos compra un Monitor (Estado: PENDIENTE)
INSERT INTO pedidos (fecha, estado, total, cliente_id) VALUES ('2023-10-02 15:45:00', 'PENDIENTE', 350.00, 2);

-- Pedido 3: Ana vuelve a comprar, esta vez unos Auriculares (Estado: ENTREGADO)
INSERT INTO pedidos (fecha, estado, total, cliente_id) VALUES ('2023-10-05 09:15:00', 'ENTREGADO', 199.99, 1);

-- Insertar Detalles de los Pedidos (Lo que hay dentro de cada caja)

-- Detalles del Pedido 1 (Laptop + Ratón)
INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
(1, 1, 1, 1200.50, 1200.50), -- 1 Laptop
(1, 2, 1, 25.99, 25.99);     -- 1 Ratón

-- Detalles del Pedido 2 (Monitor)
INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
(2, 3, 1, 350.00, 350.00);    -- 1 Monitor

-- Detalles del Pedido 3 (Auriculares)
INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES 
(3, 5, 1, 199.99, 199.99);    -- 1 Auriculares