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
      <h1>Bienvenido a Mundial Hub</h1>
      <p *ngIf="userRole">Tu rol: <strong>{{ userRole }}</strong></p>

      <!-- Paneles comunes para todos los usuarios -->
      <div class="grid">
        <a routerLink="/teams">Equipos</a>
        <a routerLink="/matches">Partidos</a>
        <a routerLink="/stickers">Álbum</a>
        <a routerLink="/communities">Comunidades</a>
        <a routerLink="/polls">Pollas</a>
      </div>

      <!-- Paneles adicionales para ADMIN y OPERATOR -->
      <div class="grid admin-panel" *ngIf="userRole === 'ADMIN' || userRole === 'OPERATOR'">
        <h3>Panel de Gestión</h3>
        <a routerLink="/admin/teams" *ngIf="userRole === 'ADMIN'">Gestionar Equipos</a>
        <a routerLink="/admin/matches" *ngIf="userRole === 'ADMIN' || userRole === 'OPERATOR'">Gestionar Partidos</a>
        <a routerLink="/admin/communities" *ngIf="userRole === 'ADMIN'">Gestionar Comunidades</a>
        <a routerLink="/admin/polls" *ngIf="userRole === 'ADMIN'">Gestionar Pollas</a>
      </div>
    </section>
  `,
  styles: [
    `.dashboard-card { padding: 24px; background: white; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,.08); }
    .grid { display: grid; gap: 12px; margin-top: 20px; }
    a { padding: 14px 18px; display: inline-block; background: #1976d2; color: white; border-radius: 10px; text-decoration: none; text-align: center; }
    .admin-panel { border-top: 2px solid #1976d2; padding-top: 20px; margin-top: 30px; }
    .admin-panel h3 { color: #1976d2; margin-bottom: 10px; }
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