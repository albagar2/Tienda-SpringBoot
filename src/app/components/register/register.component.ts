import { Component } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { Router } from '@angular/router';
import { Cliente } from '../../models/pedido.model';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    standalone: false
})
export class RegisterComponent {
    cliente: Cliente = {
        id: 0,
        nombre: '',
        email: '',
        direccion: '',
        username: '',
        password: '',
        rol: 'USER'
    };
    mensajeError = '';

    constructor(private apiService: ApiService, private router: Router) { }

    register(): void {
        this.apiService.register(this.cliente).subscribe({
            next: (res) => {
                alert('Registro exitoso. Por favor inicia sesión.');
                this.router.navigate(['/login']);
            },
            error: (err) => {
                this.mensajeError = err.error?.mensaje || 'Error al registrar';
            }
        });
    }
}
