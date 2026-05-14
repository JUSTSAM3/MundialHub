import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'sticker-album',
  imports: [CommonModule],
  template: `
    <section class="page-card">
      <h2>Álbum de stickers</h2>
      <button (click)="loadAlbum()">Cargar álbum</button>
      <div *ngIf="loading">Cargando álbum...</div>
      <div *ngIf="album?.length">
        <ul>
          <li *ngFor="let sticker of album">
            {{ sticker.name }} - {{ sticker.category }}
          </li>
        </ul>
      </div>
      <p *ngIf="!loading && !album?.length">Inicia sesión para ver tu álbum.</p>
    </section>
  `,
  styles: [
    `.page-card { padding: 24px; background: white; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,.08); }
    button { margin-bottom: 16px; padding: 10px 16px; border: none; border-radius: 10px; background: #7b1fa2; color: white; cursor: pointer; }
    ul { list-style: none; padding: 0; }
    li { padding: 10px 0; border-bottom: 1px solid #e2e8f0; }
    `]
})
export class StickerAlbumComponent implements OnInit {
  album: any[] = [];
  loading = false;

  constructor(private auth: AuthService) {}

  ngOnInit() {
    if (this.auth.isAuthenticated()) {
      this.loadAlbum();
    }
  }

  loadAlbum() {
    if (!this.auth.getToken()) return;
    this.loading = true;
    ApiService.getAlbum()
      .then((response) => {
        this.album = response.data || [];
      })
      .catch(() => {
        this.album = [];
      })
      .finally(() => {
        this.loading = false;
      });
  }
}
