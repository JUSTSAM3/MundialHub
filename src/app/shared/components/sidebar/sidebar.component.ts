import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificacionesService } from '../../../core/services/notificaciones.service';

interface NavItem {
  label: string;
  icon: string;
  ruta: string;
  roles?: string[];
  badge?: 'noLeidas';
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <aside class="sidebar">
      <div class="sidebar-logo">
        <span class="logo-icon">⚽</span>
        <span class="logo-text">MUNDIAL<br><small>2026 HUB</small></span>
      </div>

      <nav class="sidebar-nav">
        @for (item of navItems; track item.ruta) {
          @if (!item.roles || tieneRol(item.roles)) {
            <a [routerLink]="item.ruta"
               routerLinkActive="active"
               class="nav-item">
              <span class="nav-icon">{{ item.icon }}</span>
              <span class="nav-label">{{ item.label }}</span>
              @if (item.badge === 'noLeidas' && notifService.noLeidas() > 0) {
                <span class="badge-count">{{ notifService.noLeidas() }}</span>
              }
            </a>
          }
        }
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <div class="avatar">{{ iniciales() }}</div>
          <div class="user-details">
            <span class="user-name">{{ auth.usuario()?.nombre }}</span>
            <span class="user-role">{{ auth.usuario()?.rol }}</span>
          </div>
        </div>
        <button class="btn-logout" (click)="auth.logout()">⏻</button>
      </div>
    </aside>
  `,
  styles: [`
    .sidebar {
      width: var(--sidebar-width);
      height: 100vh;
      position: sticky;
      top: 0;
      background: var(--color-surface);
      border-right: 1px solid var(--color-border);
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }

    .sidebar-logo {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 24px 20px;
      border-bottom: 1px solid var(--color-border);

      .logo-icon { font-size: 28px; }
      .logo-text {
        font-family: var(--font-display);
        font-size: 18px;
        line-height: 1.1;
        color: var(--color-text);
        small { font-size: 11px; color: var(--color-accent); letter-spacing: 0.1em; }
      }
    }

    .sidebar-nav {
      flex: 1;
      padding: 16px 12px;
      display: flex;
      flex-direction: column;
      gap: 4px;
      overflow-y: auto;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 10px 12px;
      border-radius: var(--radius-md);
      color: var(--color-text-muted);
      text-decoration: none;
      font-size: 14px;
      font-weight: 500;
      transition: all 0.15s ease;
      position: relative;

      &:hover {
        background: var(--color-surface-2);
        color: var(--color-text);
        text-decoration: none;
      }

      &.active {
        background: rgba(200, 16, 46, 0.15);
        color: var(--color-primary);
        border-left: 3px solid var(--color-primary);
      }

      .nav-icon { font-size: 18px; width: 24px; text-align: center; }
      .nav-label { flex: 1; }
    }

    .badge-count {
      background: var(--color-primary);
      color: #fff;
      font-size: 11px;
      font-weight: 700;
      padding: 2px 7px;
      border-radius: var(--radius-full);
    }

    .sidebar-footer {
      padding: 16px;
      border-top: 1px solid var(--color-border);
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      flex: 1;
      overflow: hidden;
    }

    .avatar {
      width: 36px; height: 36px;
      border-radius: 50%;
      background: var(--color-primary);
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 700;
      font-size: 13px;
      flex-shrink: 0;
    }

    .user-details {
      display: flex;
      flex-direction: column;
      overflow: hidden;

      .user-name {
        font-size: 13px;
        font-weight: 600;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .user-role {
        font-size: 11px;
        color: var(--color-text-muted);
        text-transform: capitalize;
      }
    }

    .btn-logout {
      background: none;
      border: none;
      cursor: pointer;
      font-size: 18px;
      color: var(--color-text-muted);
      padding: 4px;
      border-radius: var(--radius-sm);
      transition: color 0.15s;
      &:hover { color: var(--color-danger); }
    }
  `]
})
export class SidebarComponent {
  auth = inject(AuthService);
  notifService = inject(NotificacionesService);

  navItems: NavItem[] = [
    { label: 'Dashboard',       icon: '🏠', ruta: '/dashboard' },
    { label: 'Partidos',        icon: '⚽', ruta: '/partidos' },
    { label: 'Mi Agenda',       icon: '📅', ruta: '/agenda' },
    { label: 'Entradas',        icon: '🎟️', ruta: '/entradas' },
    { label: 'Notificaciones',  icon: '🔔', ruta: '/notificaciones', badge: 'noLeidas' },
    { label: 'Pollas',          icon: '🏆', ruta: '/pollas' },
    { label: 'Álbum Digital',   icon: '📒', ruta: '/album' },
    { label: 'Mi Perfil',       icon: '👤', ruta: '/perfil' },
    { label: 'Administración',  icon: '⚙️', ruta: '/admin', roles: ['operador', 'admin', 'soporte'] }
  ];

  iniciales(): string {
    const u = this.auth.usuario();
    if (!u) return '?';
    return `${u.nombre[0]}${u.apellido[0]}`.toUpperCase();
  }

  tieneRol(roles: string[]): boolean {
    return roles.includes(this.auth.usuario()?.rol ?? '');
  }
}
