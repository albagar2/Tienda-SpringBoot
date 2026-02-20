import { Component, OnInit, ChangeDetectorRef, Inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Pedido, Cliente, Producto } from '../../models/pedido.model';

@Component({
  selector: 'app-pedidos-list',
  templateUrl: './pedidos-list.component.html',
  styleUrls: ['./pedidos-list.component.css'],
  standalone: false
})
export class PedidosListComponent implements OnInit {

  // Listas de datos
  listaPedidos: Pedido[] = [];
  listaClientes: Cliente[] = [];
  listaProductos: Producto[] = [];

  // Variables para el Formulario Nuevo
  clienteSeleccionadoId: number | null = null;
  productoSeleccionadoId: number | null = null;
  cantidadSeleccionada: number = 1;

  // Carrito temporal
  carrito: any[] = [];
  mostrarFinalizar: boolean = true;

  get totalCarrito(): number {
    return this.carrito.reduce((sum, item) => sum + item.subtotal, 0);
  }

  // Variables para Mensajes (Éxito y Error)
  mensajeExito: boolean = false;
  mensajeError: string = "";

  // Auth & Debug
  currentUser: any = null;
  debugAction: string = "Esperando acción...";

  constructor(private apiService: ApiService, private cdr: ChangeDetectorRef, private router: Router, @Inject(PLATFORM_ID) private platformId: Object) { }

  ngOnInit(): void {
    if (isPlatformBrowser(this.platformId)) {
      const userStored = localStorage.getItem('usuario');
      if (!userStored) {
        this.router.navigate(['/login']);
        return;
      }
      this.currentUser = JSON.parse(userStored);
      this.cargarDatos();
    }
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('usuario');
      this.router.navigate(['/login']);
    }
  }

  loading: boolean = true;

  cargarDatos(): void {
    this.loading = true;
    const clienteIdFilter = this.currentUser.rol === 'ADMIN' ? undefined : this.currentUser.id;

    console.log("Cargando datos...");
    this.mensajeError = "";

    // Cargar Pedidos
    this.apiService.getPedidos(clienteIdFilter).subscribe({
      next: (data) => {
        console.log("Pedidos cargados:", data);
        this.listaPedidos = data;
        this.loading = false;
        this.cdr.detectChanges(); // Forzar actualización de vista
      },
      error: (e) => {
        console.error("Error cargando pedidos:", e);
        this.mensajeError = "Error conectando con el servidor (Pedidos).";
        this.loading = false;
        this.cdr.detectChanges();
      }
    });

    // Cargar Clientes
    this.apiService.getClientes().subscribe({
      next: (data) => {
        this.listaClientes = data;
        this.cdr.detectChanges();
      },
      error: (e) => console.error("Error cargando clientes:", e)
    });

    // Cargar Productos
    this.apiService.getProductos().subscribe({
      next: (data) => {
        console.log("Productos cargados:", data);
        this.listaProductos = data;
        this.cdr.detectChanges();
      },
      error: (e) => {
        console.error("Error cargando productos:", e);
        this.mensajeError = "Error conectando con el servidor (Productos).";
        this.cdr.detectChanges();
      }
    });
  }

  agregarAlCarrito(): void {
    // Si es USER, el cliente es él mismo
    if (this.currentUser.rol === 'USER') {
      this.clienteSeleccionadoId = this.currentUser.id;
    }

    if (!this.productoSeleccionadoId) return;

    // Buscamos el producto completo por su ID
    const productoReal = this.listaProductos.find(p => p.id == this.productoSeleccionadoId);

    if (productoReal) {
      if (productoReal.stock < this.cantidadSeleccionada) {
        alert("⚠️ No hay suficiente stock. Disponibles: " + productoReal.stock);
        return;
      }
      const subtotal = productoReal.precio * this.cantidadSeleccionada;

      this.carrito.push({
        producto: productoReal,
        cantidad: this.cantidadSeleccionada,
        subtotal: subtotal
      });

      // Reseteamos inputs de producto para seguir comprando
      this.cantidadSeleccionada = 1;
      this.productoSeleccionadoId = null;
      this.mostrarFinalizar = true; // Aseguramos que se vea el botón
    }
  }

  guardarPedidoFinal(): void {
    this.debugAction = "Iniciando guardarPedidoFinal...";
    this.mensajeError = "";
    this.mensajeExito = false;

    if (this.currentUser.rol === 'USER') {
      this.clienteSeleccionadoId = this.currentUser.id;
    }

    if (this.clienteSeleccionadoId === null || this.clienteSeleccionadoId === undefined) {
      this.mensajeError = "⚠️ Error: No se ha identificado al cliente.";
      return;
    }

    if (this.carrito.length === 0) {
      this.mensajeError = "⚠️ El carrito está vacío.";
      return;
    }

    const totalPedido = this.carrito.reduce((sum, item) => sum + item.subtotal, 0);
    const nuevoPedido: any = {
      id: null,
      fecha: null,
      estado: 'PENDIENTE',
      total: totalPedido,
      cliente: { id: this.clienteSeleccionadoId },
      detalles: this.carrito.map(item => ({
        id: null,
        cantidad: item.cantidad,
        precioUnitario: item.producto.precio,
        subtotal: item.subtotal,
        producto: { id: item.producto.id }
      }))
    };

    this.apiService.savePedido(nuevoPedido).subscribe({
      next: (respuesta) => {
        this.debugAction = "¡Éxito! " + (respuesta.mensaje || "Pedido guardado.");
        this.carrito = [];
        this.productoSeleccionadoId = null;
        this.cantidadSeleccionada = 1;
        this.mostrarFinalizar = false;
        this.cdr.detectChanges();
        this.cargarDatos();
        this.mensajeExito = true;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.mensajeExito = false;
          this.cdr.detectChanges();
        }, 3000);
      },
      error: (error) => {
        this.mensajeError = "❌ Error: " + (error.error?.message || error.message);
      }
    });
  }

  eliminarPedido(id: number): void {
    if (confirm("¿Estás seguro de eliminar este pedido?")) {
      this.apiService.deletePedido(id).subscribe(() => {
        this.cargarDatos();
      });
    }
  }

  editarPedido(pedido: Pedido): void {
    this.carrito = pedido.detalles.map(detalle => ({
      producto: detalle.producto,
      cantidad: detalle.cantidad,
      subtotal: detalle.subtotal
    }));
    this.clienteSeleccionadoId = pedido.cliente.id;
    this.apiService.deletePedido(pedido.id).subscribe(() => {
      this.mensajeExito = true;
      this.cargarDatos();
    });
    this.mostrarFinalizar = true;
  }

  // --- ADMIN METHODS (STOCK & CRUD) ---

  // Estado del formulario de productos
  productoEditando: Producto = { id: 0, nombre: '', descripcion: '', precio: 0, stock: 0, imagenUrl: '' };
  mostrarFormularioProducto: boolean = false;

  iniciarEdicionProducto(producto?: Producto): void {
    if (producto) {
      this.productoEditando = { ...producto }; // Copia para no modificar la tabla directamente
    } else {
      this.productoEditando = { id: 0, nombre: '', descripcion: '', precio: 0, stock: 0, imagenUrl: '' };
    }
    this.mostrarFormularioProducto = true;
  }

  cancelarEdicionProducto(): void {
    this.mostrarFormularioProducto = false;
    this.productoEditando = { id: 0, nombre: '', descripcion: '', precio: 0, stock: 0, imagenUrl: '' };
  }

  guardarProducto(): void {
    this.mensajeError = "";
    this.mensajeExito = false;

    if (!this.productoEditando.nombre || this.productoEditando.precio < 0) {
      alert("Por favor completa los campos correctamente.");
      return;
    }

    if (this.productoEditando.id === 0) {
      // CREAR
      this.apiService.createProducto(this.productoEditando).subscribe({
        next: () => {
          this.mostrarFormularioProducto = false;
          this.cargarDatos();
          this.mensajeExito = true;
          setTimeout(() => this.mensajeExito = false, 3000);
        },
        error: (error) => {
          console.error("Error creando producto:", error);
          this.mensajeError = "❌ Error al crear producto: " + (error.error?.message || error.message);
        }
      });
    } else {
      // ACTUALIZAR
      this.apiService.updateProducto(this.productoEditando.id, this.productoEditando).subscribe({
        next: () => {
          this.mostrarFormularioProducto = false;
          this.cargarDatos();
          this.mensajeExito = true;
          setTimeout(() => this.mensajeExito = false, 3000);
        },
        error: (error) => {
          console.error("Error actualizando producto:", error);
          this.mensajeError = "❌ Error al actualizar producto: " + (error.error?.message || error.message);
        }
      });
    }
  }

  eliminarProductoInventario(id: number): void {
    if (confirm("¿Seguro que quieres eliminar este producto del inventario?")) {
      this.apiService.deleteProducto(id).subscribe(() => {
        this.cargarDatos();
      });
    }
  }

  cambiarEstado(pedido: Pedido, nuevoEstado: string): void {
    this.apiService.updatePedidoStatus(pedido.id, nuevoEstado).subscribe(() => {
      this.cargarDatos();
    });
  }

  actualizarStock(producto: Producto, nuevoStockInput: any): void {
    const nuevoStock = parseInt(nuevoStockInput.value);
    this.apiService.updateProductoStock(producto.id, nuevoStock).subscribe(() => {
      alert('Stock actualizado');
      this.cargarDatos();
    });
  }

  cancelarPedido(id: number): void {
    if (confirm("¿Estás seguro de que deseas cancelar este pedido? Se devolverá el stock al inventario.")) {
      this.apiService.cancelPedido(id).subscribe({
        next: () => {
          this.mensajeExito = true;
          this.cargarDatos();
          // Recargamos la página después de un pequeño delay para que el usuario vea el éxito
          setTimeout(() => {
            window.location.reload();
          }, 1000);
        },
        error: (err) => {
          alert("Error al cancelar: " + (err.error?.mensaje || err.message));
        }
      });
    }
  }
}

