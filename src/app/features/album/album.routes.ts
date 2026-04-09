import { Routes } from '@angular/router';
export const ALBUM_ROUTES: Routes = [
  { path: '', loadComponent: () => import('./coleccion/album-coleccion.component').then(m => m.AlbumColeccionComponent), title: 'Mi Album | Mundial 2026 Hub' },
  { path: 'intercambio', loadComponent: () => import('./intercambio/album-intercambio.component').then(m => m.AlbumIntercambioComponent), title: 'Intercambios | Mundial 2026 Hub' }
];
