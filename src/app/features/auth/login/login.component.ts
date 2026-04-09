import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <div class="auth-page">
      <div class="auth-card">
        <div class="auth-logo">⚽ MUNDIAL 2026 HUB</div>
        <h2>Iniciar Sesión</h2>
        <p class="text-muted" style="margin-bottom:24px">Bienvenido de vuelta</p>

        @if (error()) {
          <div class="alert-error">{{ error() }}</div>
        }

        <div class="form-group">
          <label>Email</label>
          <input type="email" [(ngModel)]="email" placeholder="tu@email.com" />
        </div>
        <div class="form-group">
          <label>Contraseña</label>
          <input type="password" [(ngModel)]="password" placeholder="••••••••" />
        </div>

        <button class="btn btn-primary" style="width:100%;justify-content:center;margin-top:8px"
                (click)="login()"
                [disabled]="auth.cargando()">
          {{ auth.cargando() ? 'Ingresando...' : 'Ingresar' }}
        </button>

        <p style="text-align:center;margin-top:16px;font-size:14px">
          ¿No tienes cuenta? <a routerLink="/auth/registro">Regístrate</a>
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
      max-width: 400px;
      box-shadow: var(--shadow-lg);

      h2 { font-size: 28px; margin-bottom: 4px; }
    }

    .auth-logo {
      font-family: var(--font-display);
      font-size: 20px;
      color: var(--color-accent);
      margin-bottom: 24px;
      letter-spacing: 0.05em;
    }

    .alert-error {
      background: rgba(220,53,69,0.15);
      border: 1px solid var(--color-danger);
      border-radius: var(--radius-md);
      padding: 10px 14px;
      font-size: 14px;
      color: #ff6b7a;
      margin-bottom: 16px;
    }
  `]
})
export class LoginComponent {
  auth   = inject(AuthService);
  router = inject(Router);

  email    = '';
  password = '';
  error    = signal('');

  login(): void {
    this.error.set('');
    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: resp => {
        if (resp.success) this.router.navigate(['/dashboard']);
        else this.error.set(resp.error ?? 'Error al iniciar sesión');
      },
      error: () => this.error.set('Error de conexión. Intenta nuevamente.')
    });
  }
}
