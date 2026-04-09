import { Routes } from '@angular/router';
export const POLLAS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./lista/pollas-lista.component').then(m => m.PollasListaComponent), title: 'Pollas | Mundial 2026 Hub' },
  { path: 'nueva', loadComponent: () => import('./crear/polla-crear.component').then(m => m.PollaCrearComponent), title: 'Nueva Polla | Mundial 2026 Hub' },
  { path: ':id', loadComponent: () => import('./detalle/polla-detalle.component').then(m => m.PollaDetalleComponent), title: 'Detalle Polla | Mundial 2026 Hub' }
];
