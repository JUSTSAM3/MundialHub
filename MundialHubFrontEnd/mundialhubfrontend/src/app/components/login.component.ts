import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'login-page',
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <section class="page-card">
      <h2>Iniciar sesión</h2>
      <form (submit)="onSubmit($event)">
        <label>Usuario</label>
        <input type="text" [(ngModel)]="username" name="username" required />
        <label>Contraseña</label>
        <input type="password" [(ngModel)]="password" name="password" required />
        <button type="submit">Ingresar</button>
      </form>
      <p class="small">¿No tienes cuenta? <a routerLink="/register">Regístrate</a></p>
      <p class="error" *ngIf="errorMessage">{{ errorMessage }}</p>
    </section>
  `,
  styles: [
    `.page-card { max-width: 420px; margin: 0 auto; padding: 24px; background: white; border-radius: 12px; box-shadow: 0 8px 28px rgba(0,0,0,.08); }
    form { display: grid; gap: 14px; }
    input { width: 100%; padding: 10px; border: 1px solid #cbd5e1; border-radius: 8px; }
    button { padding: 10px 16px; border: none; background: #1976d2; color: white; border-radius: 8px; cursor: pointer; }
    .small { font-size: .9rem; margin-top: 10px; }
    .error { color: #d32f2f; margin-top: 12px; }
    a { color: #1976d2; text-decoration: none; }
    `]
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';

  constructor(private auth: AuthService, private router: Router) {}

  async onSubmit(event: Event) {
    event.preventDefault();
    try {
      await this.auth.login(this.username, this.password);
      this.router.navigate(['/dashboard']);
    } catch (error: any) {
      this.errorMessage = error?.response?.data || 'Error al iniciar sesión. Revisa tus credenciales.';
    }
  }
}
