import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'register-page',
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <section class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <h1>🌍 Mundial Hub</h1>
          <p>Crea tu cuenta</p>
        </div>

        <form (submit)="onSubmit($event)" class="auth-form">
          <div class="form-group">
            <label for="username">Nombre de usuario</label>
            <input 
              type="text" 
              id="username"
              [(ngModel)]="username" 
              name="username" 
              required 
              placeholder="Elige un nombre de usuario"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input 
              type="email" 
              id="email"
              [(ngModel)]="email" 
              name="email" 
              required 
              placeholder="tu@email.com"
              class="form-input"
            />
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <input 
              type="password" 
              id="password"
              [(ngModel)]="password" 
              name="password" 
              required 
              minlength="6" 
              placeholder="Mínimo 6 caracteres"
              class="form-input"
            />
          </div>

          <button type="submit" class="btn btn-success">Registrar Cuenta</button>
        </form>

        <div class="auth-footer">
          <p>¿Ya tienes cuenta? <a routerLink="/login">Inicia sesión</a></p>
        </div>

        <p class="success-message" *ngIf="successMessage">
          ✅ {{ successMessage }}
        </p>
        <p class="error-message" *ngIf="errorMessage">
          ⚠️ {{ errorMessage }}
        </p>
      </div>
    </section>
  `,
  styles: [
    `.auth-container {
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      padding: 1rem;
      background: linear-gradient(135deg, #0f172a 0%, #1a2849 100%);
    }

    .auth-card {
      width: 100%;
      max-width: 420px;
      padding: 2.5rem;
      background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
      border: 1.5px solid rgba(6, 182, 212, 0.2);
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
    }

    .auth-header {
      text-align: center;
      margin-bottom: 2.5rem;
      padding-bottom: 2rem;
      border-bottom: 2px solid rgba(6, 182, 212, 0.2);
    }

    .auth-header h1 {
      font-size: 2rem;
      font-weight: 800;
      background: linear-gradient(135deg, #0ea5e9, #06b6d4);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 0.5rem;
    }

    .auth-header p {
      color: #cbd5e1;
      font-size: 0.95rem;
    }

    .auth-form {
      display: grid;
      gap: 1.5rem;
      margin-bottom: 1.5rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .form-group label {
      font-weight: 600;
      color: #e2e8f0;
      font-size: 0.9rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .form-input {
      padding: 0.85rem 1rem;
      border: 1.5px solid rgba(6, 182, 212, 0.3);
      border-radius: 8px;
      background: rgba(15, 23, 42, 0.5);
      color: #e2e8f0;
      font-size: 0.95rem;
      transition: all 0.3s ease;
      font-family: inherit;
    }

    .form-input::placeholder {
      color: #64748b;
    }

    .form-input:focus {
      outline: none;
      border-color: #0ea5e9;
      background: rgba(15, 23, 42, 0.8);
      box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.1);
    }

    .btn {
      padding: 0.9rem 1.5rem;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      font-size: 0.95rem;
      cursor: pointer;
      transition: all 0.3s ease;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .btn-success {
      background: linear-gradient(135deg, #10b981, #059669);
      color: white;
      box-shadow: 0 4px 15px rgba(16, 185, 129, 0.3);
    }

    .btn-success:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(16, 185, 129, 0.4);
    }

    .btn-success:active {
      transform: translateY(0);
    }

    .auth-footer {
      text-align: center;
      padding: 1.5rem;
      border-top: 1px solid rgba(6, 182, 212, 0.2);
      border-bottom: 1px solid rgba(6, 182, 212, 0.2);
      margin-bottom: 1.5rem;
    }

    .auth-footer p {
      color: #cbd5e1;
      font-size: 0.9rem;
    }

    .auth-footer a {
      color: #0ea5e9;
      text-decoration: none;
      font-weight: 600;
      transition: color 0.3s ease;
    }

    .auth-footer a:hover {
      color: #06b6d4;
      text-decoration: underline;
    }

    .success-message {
      padding: 1rem;
      background: rgba(16, 185, 129, 0.1);
      border: 1px solid rgba(16, 185, 129, 0.3);
      border-radius: 8px;
      color: #10b981;
      font-weight: 500;
      text-align: center;
      margin-top: 1rem;
    }

    .error-message {
      padding: 1rem;
      background: rgba(239, 68, 68, 0.1);
      border: 1px solid rgba(239, 68, 68, 0.3);
      border-radius: 8px;
      color: #ff6b6b;
      font-weight: 500;
      text-align: center;
      margin-top: 1rem;
    }

    @media (max-width: 480px) {
      .auth-card {
        padding: 1.5rem;
      }

      .auth-header h1 {
        font-size: 1.5rem;
      }

      .form-input {
        padding: 0.75rem 0.875rem;
      }
    }
    `]
})
export class RegisterComponent {
  username = '';
  email = '';
  password = '';
  errorMessage = '';
  successMessage = '';

  constructor(private auth: AuthService, private router: Router) {}

  async onSubmit(event: Event) {
    event.preventDefault();
    try {
      await this.auth.register({ username: this.username, email: this.email, password: this.password });
      this.successMessage = 'Registro creado. Ahora ingresa con tu cuenta.';
      this.errorMessage = '';
      setTimeout(() => this.router.navigate(['/login']), 1400);
    } catch (error: any) {
      this.errorMessage = error?.response?.data || 'Error al registrar la cuenta.';
      this.successMessage = '';
    }
  }
}
