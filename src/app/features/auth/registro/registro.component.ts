import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="auth-page">
      <div class="auth-card">
        <div class="auth-logo">⚽ MUNDIAL 2026 HUB</div>
        <h2>Crear Cuenta</h2>
        <p class="text-muted" style="margin-bottom:24px">Únete al hub del Mundial</p>

        @if (error()) {
          <div class="alert-error">{{ error() }}</div>
        }

        <div class="form-group">
          <label>Nombre</label>
          <input type="text" [(ngModel)]="nombre" placeholder="Tu nombre" />
        </div>
        <div class="form-group">
          <label>Apellido</label>
          <input type="text" [(ngModel)]="apellido" placeholder="Tu apellido" />
        </div>
        <div class="form-group">
          <label>Email</label>
          <input type="email" [(ngModel)]="email" placeholder="tu@email.com" />
        </div>
        <div class="form-group">
          <label>Contraseña</label>
          <input type="password" [(ngModel)]="password" placeholder="Mínimo 8 caracteres" />
        </div>

        <button class="btn btn-primary" style="width:100%;justify-content:center;margin-top:8px"
                (click)="registro()"
                [disabled]="auth.cargando()">
          {{ auth.cargando() ? 'Creando cuenta...' : 'Crear cuenta' }}
        </button>

        <p style="text-align:center;margin-top:16px;font-size:14px">
          ¿Ya tienes cuenta? <a routerLink="/auth/login">Inicia sesión</a>
        </p>
      </div>
    </div>
  `,
  styles: [`
    .auth-page {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: radial-gradient(ellipse at center, #1A1A2E 0%, #0D0D0D 70%);
    }
    .auth-card {
      background: var(--color-surface);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-lg);
      padding: 40px;
      width: 100%;
      max-width: 420px;
      box-shadow: var(--shadow-lg);
      h2 { font-size: 28px; margin-bottom: 4px; }
    }
    .auth-logo { font-family: var(--font-display); font-size: 20px; color: var(--color-accent); margin-bottom: 24px; letter-spacing: 0.05em; }
    .alert-error { background: rgba(220,53,69,0.15); border: 1px solid var(--color-danger); border-radius: var(--radius-md); padding: 10px 14px; font-size: 14px; color: #ff6b7a; margin-bottom: 16px; }
  `]
})
export class RegistroComponent {
  auth   = inject(AuthService);
  router = inject(Router);

  nombre   = '';
  apellido = '';
  email    = '';
  password = '';
  error    = signal('');

  registro(): void {
    this.error.set('');
    this.auth.registro({ nombre: this.nombre, apellido: this.apellido, email: this.email, password: this.password })
      .subscribe({
        next: resp => {
          if (resp.success) this.router.navigate(['/dashboard']);
          else this.error.set(resp.error ?? 'Error al crear la cuenta');
        },
        error: () => this.error.set('Error de conexión.')
      });
  }
}
