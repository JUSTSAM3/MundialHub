import { Routes } from '@angular/router';
export const ENTRADAS_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./lista/entradas-lista.component').then(m => m.EntradasListaComponent), title: 'Mis Entradas | Mundial 2026 Hub' },
  { path: ':id', loadComponent: () => import('./detalle/entrada-detalle.component').then(m => m.EntradaDetalleComponent), title: 'Detalle Entrada | Mundial 2026 Hub' }
];
