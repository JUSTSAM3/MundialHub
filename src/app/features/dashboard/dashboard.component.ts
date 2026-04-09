import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { PartidosService } from '../../core/services/partidos.service';
import { Partido } from '../../core/models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="fade-in">
      <div class="page-header">
        <h1>Bienvenido, {{ auth.usuario()?.nombre }} 👋</h1>
        <p>Tu centro de control para el Mundial 2026</p>
      </div>

      <!-- Stats -->
      <div class="stats-grid">
        <div class="stat-card">
          <span class="stat-icon">⚽</span>
          <div>
            <p class="stat-value">48</p>
            <p class="stat-label">Partidos en agenda</p>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">🎟️</span>
          <div>
            <p class="stat-value">2</p>
            <p class="stat-label">Entradas activas</p>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">🏆</span>
          <div>
            <p class="stat-value">3</p>
            <p class="stat-label">Pollas activas</p>
          </div>
        </div>
        <div class="stat-card">
          <span class="stat-icon">📒</span>
          <div>
            <p class="stat-value">127/640</p>
            <p class="stat-label">Láminas del álbum</p>
          </div>
        </div>
      </div>

      <!-- Próximos partidos -->
      <section class="section">
        <div class="section-header">
          <h2>Próximos Partidos</h2>
          <a routerLink="/partidos" class="btn btn-outline" style="font-size:12px;padding:6px 14px">Ver todos →</a>
        </div>

        @if (cargando()) {
          <p class="text-muted">Cargando partidos...</p>
        } @else {
          <div class="partidos-list">
            @for (partido of proximosPartidos(); track partido.id) {
              <div class="partido-card">
                <div class="partido-equipos">
                  <span class="equipo">{{ partido.equipoLocal.nombre }}</span>
                  <span class="vs">VS</span>
                  <span class="equipo">{{ partido.equipoVisitante.nombre }}</span>
                </div>
                <div class="partido-info">
                  <span class="badge badge-soon">{{ partido.fase }}</span>
                  <span class="text-muted" style="font-size:12px">{{ partido.estadio.ciudad }}</span>
                  <span class="text-muted" style="font-size:12px">{{ partido.fechaHora | date:'dd/MM HH:mm' }}</span>
                </div>
              </div>
            } @empty {
              <p class="text-muted">No hay partidos próximos registrados aún.</p>
            }
          </div>
        }
      </section>

      <!-- Accesos rápidos -->
      <section class="section">
        <h2>Accesos Rápidos</h2>
        <div class="quick-actions">
          <a routerLink="/pollas" class="quick-card">
            <span>🏆</span>
            <strong>Mis Pollas</strong>
            <small>Ver predicciones y ranking</small>
          </a>
          <a routerLink="/album" class="quick-card">
            <span>📒</span>
            <strong>Álbum Digital</strong>
            <small>Abrir paquetes e intercambiar</small>
          </a>
          <a routerLink="/entradas" class="quick-card">
            <span>🎟️</span>
            <strong>Mis Entradas</strong>
            <small>Gestionar y transferir</small>
          </a>
          <a routerLink="/agenda" class="quick-card">
            <span>📅</span>
            <strong>Mi Agenda</strong>
            <small>Ver calendario personalizado</small>
          </a>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: var(--spacing-md);
      margin-bottom: var(--spacing-xl);
    }

    .stat-card {
      background: var(--color-surface);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-lg);
      padding: var(--spacing-lg);
      display: flex;
      align-items: center;
      gap: var(--spacing-md);

      .stat-icon { font-size: 32px; }
      .stat-value { font-family: var(--font-display); font-size: 28px; line-height: 1; }
      .stat-label { font-size: 12px; color: var(--color-text-muted); margin-top: 4px; }
    }

    .section { margin-bottom: var(--spacing-2xl); }
    .section-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: var(--spacing-md);

      h2 { font-size: 22px; }
    }

    .partidos-list { display: flex; flex-direction: column; gap: var(--spacing-sm); }

    .partido-card {
      background: var(--color-surface);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-md);
      padding: var(--spacing-md) var(--spacing-lg);
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: var(--spacing-md);

      &:hover { border-color: var(--color-accent); }
    }

    .partido-equipos {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);

      .equipo { font-weight: 600; font-size: 15px; }
      .vs { color: var(--color-text-muted); font-size: 12px; font-weight: 700; }
    }

    .partido-info {
      display: flex;
      align-items: center;
      gap: var(--spacing-md);
    }

    .quick-actions {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
      gap: var(--spacing-md);
    }

    .quick-card {
      background: var(--color-surface);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-lg);
      padding: var(--spacing-lg);
      display: flex;
      flex-direction: column;
      gap: var(--spacing-sm);
      text-decoration: none;
      color: var(--color-text);
      transition: all 0.2s ease;

      span { font-size: 28px; }
      strong { font-size: 15px; }
      small { font-size: 12px; color: var(--color-text-muted); }

      &:hover {
        border-color: var(--color-accent);
        transform: translateY(-2px);
        text-decoration: none;
      }
    }
  `]
})
export class DashboardComponent implements OnInit {
  auth = inject(AuthService);
  private partidosService = inject(PartidosService);

  proximosPartidos = signal<Partido[]>([]);
  cargando = signal(false);

  ngOnInit(): void {
    this.cargarProximosPartidos();
  }

  private cargarProximosPartidos(): void {
    this.cargando.set(true);
    this.partidosService.getProximosPartidos(5).subscribe({
      next: resp => {
        if (resp.success && resp.data) {
          this.proximosPartidos.set(resp.data);
        }
        this.cargando.set(false);
      },
      error: () => {
        // Degradación con gracia: mostramos lista vacía
        this.cargando.set(false);
      }
    });
  }
}
