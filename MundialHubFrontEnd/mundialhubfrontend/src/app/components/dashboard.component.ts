import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'dashboard-page',
  imports: [CommonModule, RouterLink],
  template: `
    <section class="dashboard-card">
      <div class="dashboard-header">
        <h1>🌍 Bienvenido a Mundial Hub</h1>
        <p *ngIf="userRole" class="user-role">Rol: <strong>{{ userRole }}</strong></p>
      </div>

      <!-- Paneles comunes para todos los usuarios -->
      <div class="section-title">Navegación Principal</div>
      <div class="grid grid-4">
        <a routerLink="/teams" class="card-link">
          <span class="icon">⚽</span>
          <span>Equipos</span>
        </a>
        <a routerLink="/matches" class="card-link">
          <span class="icon">🎯</span>
          <span>Partidos</span>
        </a>
        <a routerLink="/stickers" class="card-link">
          <span class="icon">🎖️</span>
          <span>Álbum</span>
        </a>
        <a routerLink="/communities" class="card-link">
          <span class="icon">👥</span>
          <span>Comunidades</span>
        </a>
        <a routerLink="/polls" class="card-link">
          <span class="icon">🗳️</span>
          <span>Pollas</span>
        </a>
      </div>

      <!-- Paneles adicionales para ADMIN y OPERATOR -->
      <div *ngIf="userRole === 'ADMIN' || userRole === 'OPERATOR'" class="admin-section">
        <div class="section-title">⚙️ Panel de Gestión</div>
        <div class="grid grid-4">
          <a routerLink="/admin/teams" *ngIf="userRole === 'ADMIN'" class="card-link admin-link">
            <span class="icon">🏆</span>
            <span>Gestionar Equipos</span>
          </a>
          <a routerLink="/admin/matches" *ngIf="userRole === 'ADMIN' || userRole === 'OPERATOR'" class="card-link admin-link">
            <span class="icon">📋</span>
            <span>Gestionar Partidos</span>
          </a>
          <a routerLink="/admin/communities" *ngIf="userRole === 'ADMIN'" class="card-link admin-link">
            <span class="icon">👥</span>
            <span>Gestionar Comunidades</span>
          </a>
          <a routerLink="/admin/polls" *ngIf="userRole === 'ADMIN'" class="card-link admin-link">
            <span class="icon">⚙️</span>
            <span>Gestionar Pollas</span>
          </a>
        </div>
      </div>
    </section>
  `,
  styles: [
    `.dashboard-card {
      padding: 2.5rem;
      background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
      border-radius: 16px;
      border: 1px solid rgba(6, 182, 212, 0.15);
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
    }

    .dashboard-header {
      margin-bottom: 2.5rem;
      padding-bottom: 2rem;
      border-bottom: 2px solid rgba(6, 182, 212, 0.2);
    }

    .dashboard-header h1 {
      font-size: 2rem;
      font-weight: 800;
      background: linear-gradient(135deg, #0ea5e9, #06b6d4);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      margin-bottom: 0.75rem;
    }

    .user-role {
      color: #cbd5e1;
      font-size: 0.95rem;
    }

    .user-role strong {
      color: #06b6d4;
      font-weight: 600;
    }

    .section-title {
      font-size: 1.1rem;
      font-weight: 700;
      color: #0ea5e9;
      margin: 2rem 0 1.5rem 0;
      text-transform: uppercase;
      letter-spacing: 1px;
    }

    .grid {
      display: grid;
      gap: 1.25rem;
      margin-bottom: 2rem;
    }

    .grid-4 {
      grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    }

    .card-link {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 0.75rem;
      padding: 1.75rem;
      background: linear-gradient(135deg, rgba(6, 182, 212, 0.1) 0%, rgba(14, 165, 233, 0.05) 100%);
      border: 1.5px solid rgba(6, 182, 212, 0.3);
      border-radius: 12px;
      color: #06b6d4;
      text-decoration: none;
      font-weight: 600;
      font-size: 0.95rem;
      text-align: center;
      transition: all 0.3s ease;
      cursor: pointer;
      text-transform: capitalize;
    }

    .card-link:hover {
      background: linear-gradient(135deg, rgba(6, 182, 212, 0.2) 0%, rgba(14, 165, 233, 0.1) 100%);
      border-color: rgba(6, 182, 212, 0.6);
      transform: translateY(-4px);
      box-shadow: 0 10px 30px rgba(6, 182, 212, 0.2);
    }

    .icon {
      font-size: 2rem;
      display: block;
    }

    .admin-section {
      margin-top: 3rem;
      padding-top: 2rem;
      border-top: 2px solid rgba(249, 115, 22, 0.2);
    }

    .card-link.admin-link {
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.1) 0%, rgba(245, 158, 11, 0.05) 100%);
      border-color: rgba(249, 115, 22, 0.3);
      color: #f97316;
    }

    .card-link.admin-link:hover {
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.2) 0%, rgba(245, 158, 11, 0.1) 100%);
      border-color: rgba(249, 115, 22, 0.6);
      box-shadow: 0 10px 30px rgba(249, 115, 22, 0.2);
    }

    @media (max-width: 768px) {
      .dashboard-card {
        padding: 1.5rem;
      }

      .dashboard-header h1 {
        font-size: 1.5rem;
      }

      .grid-4 {
        grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
      }

      .card-link {
        padding: 1.25rem;
        font-size: 0.85rem;
      }

      .icon {
        font-size: 1.5rem;
      }
    }
    `]
})
export class DashboardComponent implements OnInit {
  userRole: string | null = null;

  constructor(private auth: AuthService) {}

  ngOnInit() {
    const role = this.auth.getUserRole();
    this.userRole = Array.isArray(role) ? role[0] : role;
  }
}