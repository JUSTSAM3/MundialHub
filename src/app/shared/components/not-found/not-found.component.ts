import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div style="display:flex;flex-direction:column;align-items:center;justify-content:center;height:100vh;gap:16px;text-align:center">
      <div style="font-size:80px">⚽</div>
      <h1 style="font-family:var(--font-display);font-size:48px">404</h1>
      <p style="color:var(--color-text-muted)">Esta página no existe en el Mundial 2026 Hub.</p>
      <a routerLink="/dashboard" class="btn btn-primary">Volver al inicio</a>
    </div>
  `
})
export class NotFoundComponent {}
