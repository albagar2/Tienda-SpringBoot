import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pedido } from '../models/pedido.model'; // Asegúrate de que la ruta sea correcta
import { Producto } from '../models/pedido.model';
import { Cliente } from '../models/pedido.model';


@Injectable({
  providedIn: 'root'
})
export class ApiService {

  // URL de tu Backend Spring Boot
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  // Método para obtener los pedidos
  getPedidos(clienteId?: number): Observable<Pedido[]> {
    let url = `${this.baseUrl}/pedidos`;
    if (clienteId) {
      url += `?clienteId=${clienteId}`;
    }
    return this.http.get<Pedido[]>(url);
  }

  // Método para guardar un nuevo pedido (POST)
  savePedido(pedido: Pedido): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/pedidos`, pedido);
  }

  cancelPedido(id: number): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/pedidos/${id}/cancelar`, {});
  }

  deletePedido(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/pedidos/${id}`);
  }

  getClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.baseUrl}/clientes`);
  }

  getProductos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.baseUrl}/productos`);
  }

  // --- AUTH ---
  login(credenciales: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/login`, credenciales);
  }

  register(cliente: Cliente): Observable<any> {
    return this.http.post(`${this.baseUrl}/auth/register`, cliente);
  }

  // --- ADMIN ---
  updatePedidoStatus(id: number, estado: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/pedidos/${id}/estado`, { estado });
  }

  updateProductoStock(id: number, stock: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/productos/${id}/stock`, { stock });
  }

  // --- CRUD PRODUCTOS ---
  createProducto(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(`${this.baseUrl}/productos`, producto);
  }

  updateProducto(id: number, producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${this.baseUrl}/productos/${id}`, producto);
  }

  deleteProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/productos/${id}`);
  }
}
