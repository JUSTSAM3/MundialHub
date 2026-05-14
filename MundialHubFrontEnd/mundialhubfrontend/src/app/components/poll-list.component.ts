import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'poll-list',
  imports: [CommonModule],
  template: `
    <section class="page-container">
      <div class="page-header">
        <h2>🗳️ Pollas</h2>
        <p class="page-subtitle">Participa en las pollas de tu comunidad</p>
      </div>

      <button (click)="loadPolls()" class="btn-load">📥 Cargar mis pollas</button>

      <div *ngIf="loading" class="loading-state">
        <span class="spinner"></span>
        <p>Cargando pollas...</p>
      </div>

      <div *ngIf="polls?.length" class="content-card">
        <ul class="polls-list">
          <li *ngFor="let poll of polls" class="poll-item">
            <div class="poll-info">
              <div class="poll-icon">🗳️</div>
              <div class="poll-details">
                <strong class="poll-name">{{ poll.name }}</strong>
                <span class="poll-community">Comunidad: {{ poll.communityName || 'N/A' }}</span>
              </div>
            </div>
          </li>
        </ul>
      </div>

      <div *ngIf="!loading && !polls?.length" class="content-card empty-state">
        <div class="empty-icon">🔐</div>
        <p class="empty-text">Inicia sesión para ver tus pollas.</p>
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
      background: linear-gradient(135deg, #8b5cf6, #7c3aed);
      color: white;
      font-weight: 600;
      font-size: 0.95rem;
      cursor: pointer;
      transition: all 0.3s ease;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      box-shadow: 0 4px 15px rgba(139, 92, 246, 0.3);
    }

    .btn-load:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(139, 92, 246, 0.4);
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

    .polls-list {
      list-style: none;
      padding: 0;
    }

    .poll-item {
      padding: 1.5rem;
      margin-bottom: 1rem;
      background: rgba(139, 92, 246, 0.08);
      border: 1px solid rgba(139, 92, 246, 0.25);
      border-radius: 12px;
      transition: all 0.3s ease;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .poll-item:last-child {
      margin-bottom: 0;
    }

    .poll-item:hover {
      background: rgba(139, 92, 246, 0.15);
      border-color: rgba(139, 92, 246, 0.5);
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
    }

    .poll-info {
      display: flex;
      align-items: center;
      gap: 1rem;
      flex: 1;
    }

    .poll-icon {
      font-size: 1.75rem;
      min-width: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .poll-details {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .poll-name {
      color: #8b5cf6;
      font-weight: 700;
      font-size: 1rem;
    }

    .poll-community {
      color: #cbd5e1;
      font-size: 0.85rem;
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

      .poll-item {
        padding: 1rem;
        flex-direction: column;
        text-align: center;
        gap: 0.75rem;
      }

      .poll-info {
        width: 100%;
      }
    }
    `]
})
export class PollListComponent implements OnInit {
  polls: any[] = [];
  loading = false;

  constructor(private auth: AuthService) {}

  ngOnInit() {
    if (this.auth.isAuthenticated()) {
      this.loadPolls();
    }
  }

  loadPolls() {
    if (!this.auth.getToken()) return;
    this.loading = true;
    ApiService.getPolls()
      .then((response) => {
        this.polls = response.data || [];
      })
      .catch(() => {
        this.polls = [];
      })
      .finally(() => {
        this.loading = false;
      });
  }
}
