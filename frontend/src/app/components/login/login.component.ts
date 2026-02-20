import { Component, Inject, PLATFORM_ID } from '@angular/core';
import { ApiService } from '../../services/api.service';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    standalone: false
})
export class LoginComponent {
    credenciales = { username: '', password: '' };
    mensajeError = '';

    constructor(private apiService: ApiService, private router: Router, @Inject(PLATFORM_ID) private platformId: Object) { }

    login(): void {
        this.apiService.login(this.credenciales).subscribe({
            next: (res) => {
                if (isPlatformBrowser(this.platformId)) {
                    localStorage.setItem('usuario', JSON.stringify(res.usuario));
                }
                this.router.navigate(['/pedidos']);
            },
            error: (err) => {
                this.mensajeError = err.error?.mensaje || 'Error al iniciar sesión';
            }
        });
    }
}
