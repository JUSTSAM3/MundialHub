import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'sticker-album',
  imports: [CommonModule],
  template: `
    <section class="page-container">
      <div class="page-header">
        <h2>🎖️ Álbum de Stickers</h2>
        <p class="page-subtitle">Colecciona todos los stickers del torneo</p>
      </div>

      <button (click)="loadAlbum()" class="btn-load">📥 Cargar mi álbum</button>

      <div *ngIf="loading" class="loading-state">
        <span class="spinner"></span>
        <p>Cargando álbum...</p>
      </div>

      <div *ngIf="album?.length" class="content-card">
        <div class="stickers-grid">
          <div *ngFor="let sticker of album" class="sticker-item">
            <div class="sticker-card">
              <div class="sticker-icon">🎖️</div>
              <div class="sticker-info">
                <p class="sticker-name">{{ sticker.name }}</p>
                <span class="sticker-category">{{ sticker.category }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div *ngIf="!loading && !album?.length" class="content-card empty-state">
        <div class="empty-icon">🔐</div>
        <p class="empty-text">Inicia sesión para ver tu álbum.</p>
      </div>
    </section>
  `,
  styles: [
    `.page-container {
      padding: 0;
    }

    .page-header {
      margin-bottom: 2.5rem;
      padding-bottom: 2rem;
      border-bottom: 2px solid rgba(6, 182, 212, 0.2);
    }

    .page-header h2 {
      font-size: 1.8rem;
      font-weight: 800;
      color: #e2e8f0;
      margin-bottom: 0.5rem;
    }

    .page-subtitle {
      color: #cbd5e1;
      font-size: 0.95rem;
    }

    .btn-load {
      margin-bottom: 2rem;
      padding: 0.9rem 1.75rem;
      border: none;
      border-radius: 8px;
      background: linear-gradient(135deg, #ec4899, #db2777);
      color: white;
      font-weight: 600;
      font-size: 0.95rem;
      cursor: pointer;
      transition: all 0.3s ease;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      box-shadow: 0 4px 15px rgba(236, 72, 153, 0.3);
    }

    .btn-load:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(236, 72, 153, 0.4);
    }

    .loading-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 3rem 2rem;
      color: #cbd5e1;
      gap: 1rem;
    }

    .spinner {
      display: inline-block;
      width: 30px;
      height: 30px;
      border: 3px solid rgba(6, 182, 212, 0.2);
      border-top-color: #06b6d4;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    .content-card {
      padding: 2rem;
      background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
      border: 1px solid rgba(6, 182, 212, 0.15);
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    }

    .stickers-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 1.25rem;
    }

    .sticker-item {
      position: relative;
    }

    .sticker-card {
      padding: 1.5rem;
      background: linear-gradient(135deg, rgba(236, 72, 153, 0.1), rgba(219, 39, 119, 0.08));
      border: 1.5px solid rgba(236, 72, 153, 0.25);
      border-radius: 12px;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.75rem;
      text-align: center;
      transition: all 0.3s ease;
      cursor: pointer;
      height: 100%;
    }

    .sticker-card:hover {
      background: linear-gradient(135deg, rgba(236, 72, 153, 0.15), rgba(219, 39, 119, 0.12));
      border-color: rgba(236, 72, 153, 0.5);
      transform: translateY(-4px) scale(1.02);
      box-shadow: 0 8px 20px rgba(236, 72, 153, 0.2);
    }

    .sticker-icon {
      font-size: 2.5rem;
    }

    .sticker-info {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
      width: 100%;
    }

    .sticker-name {
      color: #ec4899;
      font-weight: 700;
      font-size: 0.95rem;
      margin: 0;
    }

    .sticker-category {
      color: #cbd5e1;
      font-size: 0.8rem;
      font-weight: 500;
    }

    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 4rem 2rem;
      text-align: center;
    }

    .empty-icon {
      font-size: 3.5rem;
      margin-bottom: 1rem;
      opacity: 0.6;
    }

    .empty-text {
      color: #cbd5e1;
      font-size: 1rem;
    }

    @media (max-width: 768px) {
      .page-header h2 {
        font-size: 1.5rem;
      }

      .content-card {
        padding: 1.25rem;
      }

      .stickers-grid {
        grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
        gap: 1rem;
      }
    }
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
