import { Component } from '@angular/core';

@Component({
  selector: 'app-stub',
  standalone: true,
  template: `
    <div class="fade-in">
      <div class="page-header">
        <h1>📒 Mi Album</h1>
        <p>Coleccion de laminas</p>
      </div>
      <div class="card" style="text-align:center;padding:48px">
        <p style="font-size:48px">🚧</p>
        <h3 style="font-family:var(--font-display);font-size:22px;margin:12px 0">En desarrollo</h3>
        <p style="color:var(--color-text-muted)">Este módulo está listo para ser implementado.</p>
      </div>
    </div>
  `
})
export class AlbumColeccionComponent {}
