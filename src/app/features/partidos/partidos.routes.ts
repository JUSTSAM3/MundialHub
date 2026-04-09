import { Routes } from '@angular/router';
export const PARTIDOS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./lista/partidos-lista.component').then(m => m.PartidosListaComponent), title: 'Partidos | Mundial 2026 Hub' },
  { path: ':id', loadComponent: () => import('./detalle/partido-detalle.component').then(m => m.PartidoDetalleComponent), title: 'Detalle Partido | Mundial 2026 Hub' }
];
