import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'admin-dashboard',
  imports: [CommonModule, RouterLink],
  template: `
    <section class="admin-dashboard">
      <div class="admin-header">
        <h1>⚙️ Panel de Administración</h1>
        <p class="admin-subtitle">Gestiona todos los aspectos de Mundial Hub</p>
      </div>

      <div class="admin-grid">
        <div class="admin-card" routerLink="/admin/users">
          <div class="card-icon">👥</div>
          <h3>Gestión de Usuarios</h3>
          <p>Crear, editar, eliminar y asignar roles a usuarios</p>
          <span class="badge">{{ userCount }}</span>
        </div>

        <div class="admin-card" routerLink="/admin/stats">
          <div class="card-icon">📊</div>
          <h3>Estadísticas</h3>
          <p>Ver reportes y estadísticas del sistema</p>
        </div>

        <div class="admin-card" routerLink="/admin/roles">
          <div class="card-icon">🔐</div>
          <h3>Gestión de Roles</h3>
          <p>Administrar roles y permisos</p>
        </div>

        <div class="admin-card" routerLink="/admin/system">
          <div class="card-icon">🛠️</div>
          <h3>Configuración del Sistema</h3>
          <p>Ajustes generales de la plataforma</p>
        </div>

        <div class="admin-card" routerLink="/admin/logs">
          <div class="card-icon">📋</div>
          <h3>Registros de Actividad</h3>
          <p>Ver historial de acciones del sistema</p>
        </div>

        <div class="admin-card" routerLink="/admin/security">
          <div class="card-icon">🔒</div>
          <h3>Seguridad</h3>
          <p>Gestionar seguridad y permisos</p>
        </div>
      </div>

      <div class="admin-stats">
        <div class="stat-box">
          <span class="stat-label">Total de Usuarios</span>
          <span class="stat-value">{{ userCount }}</span>
        </div>
        <div class="stat-box">
          <span class="stat-label">Administradores</span>
          <span class="stat-value">{{ adminCount }}</span>
        </div>
        <div class="stat-box">
          <span class="stat-label">Operadores</span>
          <span class="stat-value">{{ operatorCount }}</span>
        </div>
        <div class="stat-box">
          <span class="stat-label">Usuarios Activos</span>
          <span class="stat-value">{{ activeCount }}</span>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .admin-dashboard {
      padding: 0;
    }

    .admin-header {
      margin-bottom: 2.5rem;
      padding-bottom: 2rem;
      border-bottom: 2px solid rgba(249, 115, 22, 0.2);
    }

    .admin-header h1 {
      font-size: 2rem;
      font-weight: 800;
      color: #e2e8f0;
      margin-bottom: 0.5rem;
    }

    .admin-subtitle {
      color: #cbd5e1;
      font-size: 0.95rem;
    }

    .admin-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 1.75rem;
      margin-bottom: 3rem;
    }

    .admin-card {
      padding: 2rem;
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.1), rgba(245, 158, 11, 0.08));
      border: 1.5px solid rgba(249, 115, 22, 0.3);
      border-radius: 14px;
      cursor: pointer;
      transition: all 0.3s ease;
      position: relative;
      overflow: hidden;
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .admin-card::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.05), transparent);
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    .admin-card:hover {
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.15), rgba(245, 158, 11, 0.12));
      border-color: rgba(249, 115, 22, 0.6);
      transform: translateY(-6px);
      box-shadow: 0 12px 35px rgba(249, 115, 22, 0.25);
    }

    .admin-card:hover::before {
      opacity: 1;
    }

    .card-icon {
      font-size: 2.5rem;
      display: block;
    }

    .admin-card h3 {
      font-size: 1.1rem;
      font-weight: 700;
      color: #f97316;
      margin: 0;
    }

    .admin-card p {
      color: #cbd5e1;
      font-size: 0.85rem;
      margin: 0;
      line-height: 1.4;
      flex: 1;
    }

    .badge {
      display: inline-block;
      padding: 0.4rem 0.8rem;
      background: rgba(249, 115, 22, 0.2);
      color: #f97316;
      border-radius: 6px;
      font-size: 0.8rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      align-self: flex-start;
    }

    .admin-stats {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1.5rem;
      padding: 2rem;
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.08), rgba(245, 158, 11, 0.05));
      border: 1px solid rgba(249, 115, 22, 0.15);
      border-radius: 14px;
    }

    .stat-box {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      text-align: center;
      padding: 1rem;
      background: rgba(249, 115, 22, 0.05);
      border-radius: 10px;
    }

    .stat-label {
      color: #cbd5e1;
      font-size: 0.85rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      font-weight: 500;
    }

    .stat-value {
      color: #f97316;
      font-size: 2rem;
      font-weight: 800;
    }

    @media (max-width: 768px) {
      .admin-header h1 {
        font-size: 1.5rem;
      }

      .admin-grid {
        grid-template-columns: 1fr;
        gap: 1rem;
      }

      .admin-stats {
        grid-template-columns: repeat(2, 1fr);
      }
    }
  `]
})
export class AdminDashboardComponent implements OnInit {
  userCount = 0;
  adminCount = 0;
  operatorCount = 0;
  activeCount = 0;

  constructor(private auth: AuthService) {}

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    // Estos datos se cargarían del backend en una implementación real
    this.userCount = 48;
    this.adminCount = 3;
    this.operatorCount = 5;
    this.activeCount = 42;
  }
}
