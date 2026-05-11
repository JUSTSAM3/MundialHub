import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'community-list',
  imports: [CommonModule],
  template: `
    <section class="page-container">
      <div class="page-header">
        <h2>👥 Comunidades</h2>
        <p class="page-subtitle">Únete a comunidades y comparte tu pasión</p>
      </div>

      <button (click)="loadCommunities()" class="btn-load">📥 Cargar mis comunidades</button>

      <div *ngIf="loading" class="loading-state">
        <span class="spinner"></span>
        <p>Cargando comunidades...</p>
      </div>

      <div *ngIf="communities?.length" class="content-card">
        <ul class="communities-list">
          <li *ngFor="let community of communities" class="community-item">
            <div class="community-info">
              <div class="community-icon">👥</div>
              <div class="community-details">
                <strong class="community-name">{{ community.name }}</strong>
                <span class="community-description">{{ community.description }}</span>
              </div>
            </div>
            <div class="community-badge">Active</div>
          </li>
        </ul>
      </div>

      <div *ngIf="!loading && !communities?.length" class="content-card empty-state">
        <div class="empty-icon">🔐</div>
        <p class="empty-text">Inicia sesión para ver tus comunidades.</p>
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
      background: linear-gradient(135deg, #0ea5e9, #06b6d4);
      color: white;
      font-weight: 600;
      font-size: 0.95rem;
      cursor: pointer;
      transition: all 0.3s ease;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      box-shadow: 0 4px 15px rgba(6, 182, 212, 0.3);
    }

    .btn-load:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(6, 182, 212, 0.4);
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

    .communities-list {
      list-style: none;
      padding: 0;
    }

    .community-item {
      padding: 1.5rem;
      margin-bottom: 1rem;
      background: rgba(6, 182, 212, 0.05);
      border: 1px solid rgba(6, 182, 212, 0.2);
      border-radius: 12px;
      transition: all 0.3s ease;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 1rem;
    }

    .community-item:last-child {
      margin-bottom: 0;
    }

    .community-item:hover {
      background: rgba(6, 182, 212, 0.1);
      border-color: rgba(6, 182, 212, 0.4);
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(6, 182, 212, 0.15);
    }

    .community-info {
      display: flex;
      align-items: center;
      gap: 1rem;
      flex: 1;
    }

    .community-icon {
      font-size: 1.75rem;
      min-width: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .community-details {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .community-name {
      color: #0ea5e9;
      font-weight: 700;
      font-size: 1rem;
    }

    .community-description {
      color: #cbd5e1;
      font-size: 0.85rem;
      font-weight: 500;
      line-height: 1.3;
    }

    .community-badge {
      padding: 0.4rem 0.75rem;
      background: rgba(16, 185, 129, 0.15);
      color: #10b981;
      border-radius: 6px;
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      white-space: nowrap;
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

      .community-item {
        padding: 1rem;
        flex-direction: column;
        align-items: flex-start;
      }

      .community-badge {
        align-self: flex-start;
      }
    }
    `]
})
export class CommunityListComponent implements OnInit {
  communities: any[] = [];
  loading = false;

  constructor(private auth: AuthService) {}

  ngOnInit() {
    if (this.auth.isAuthenticated()) {
      this.loadCommunities();
    }
  }

  loadCommunities() {
    if (!this.auth.getToken()) return;
    this.loading = true;
    ApiService.getCommunities()
      .then((response) => {
        this.communities = response.data || [];
      })
      .catch(() => {
        this.communities = [];
      })
      .finally(() => {
        this.loading = false;
      });
  }
}
