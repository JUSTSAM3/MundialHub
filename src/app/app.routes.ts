import { Routes } from '@angular/router';
import { authGuard, operadorGuard, soporteGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // ---- Redirección raíz ----
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },

  // ---- Auth (sin layout) ----
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },

  // ---- App principal (con layout) ----
  {
    path: '',
    //canActivate: [authGuard],
    loadComponent: () => import('./shared/components/layout/layout.component').then(m => m.LayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
        title: 'Dashboard | Mundial 2026 Hub'
      },
      {
        path: 'partidos',
        loadChildren: () => import('./features/partidos/partidos.routes').then(m => m.PARTIDOS_ROUTES)
      },
      {
        path: 'agenda',
        loadComponent: () => import('./features/agenda/agenda.component').then(m => m.AgendaComponent),
        title: 'Mi Agenda | Mundial 2026 Hub'
      },
      {
        path: 'entradas',
        loadChildren: () => import('./features/entradas/entradas.routes').then(m => m.ENTRADAS_ROUTES)
      },
      {
        path: 'notificaciones',
        loadComponent: () => import('./features/notificaciones/notificaciones.component').then(m => m.NotificacionesComponent),
        title: 'Notificaciones | Mundial 2026 Hub'
      },
      {
        path: 'pollas',
        loadChildren: () => import('./features/pollas/pollas.routes').then(m => m.POLLAS_ROUTES)
      },
      {
        path: 'album',
        loadChildren: () => import('./features/album/album.routes').then(m => m.ALBUM_ROUTES)
      },
      {
        path: 'perfil',
        loadComponent: () => import('./features/perfil/perfil.component').then(m => m.PerfilComponent),
        title: 'Mi Perfil | Mundial 2026 Hub'
      },
      // ---- Backoffice (solo operadores/soporte) ----
      {
        path: 'admin',
        canActivate: [operadorGuard],
        loadChildren: () => import('./features/admin/admin.routes').then(m => m.ADMIN_ROUTES)
      }
    ]
  },

  // ---- 404 ----
  {
    path: '**',
    loadComponent: () => import('./shared/components/not-found/not-found.component').then(m => m.NotFoundComponent)
  }
];
