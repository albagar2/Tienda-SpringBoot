export interface Cliente {
    id: number;
    nombre: string;
    email: string;
    direccion: string;
    username?: string;
    password?: string;
    rol?: 'ADMIN' | 'USER';
}

export interface Producto {
    id: number;
    nombre: string;
    descripcion: string;
    precio: number;
    stock: number;
    imagenUrl: string;
}

export interface DetallePedido {
    id: number;
    cantidad: number;
    precioUnitario: number;
    subtotal: number;
    producto: Producto;
}

export interface Pedido {
    id: number;
    fecha: string;
    estado: string;
    total: number;
    cliente: Cliente;
    detalles: DetallePedido[];
}
