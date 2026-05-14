import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';

@Component({
  standalone: true,
  selector: 'match-list',
  imports: [CommonModule],
  template: `
    <section class="page-container">
      <div class="page-header">
        <h2>🎯 Partidos del Torneo</h2>
        <p class="page-subtitle">Sigue todos los partidos en vivo</p>
      </div>

      <div *ngIf="matches?.length; else empty" class="content-card">
        <ul class="matches-list">
          <li *ngFor="let match of matches" class="match-item">
            <div class="match-teams">
              <span class="team">{{ match.homeTeam }}</span>
              <span class="vs">vs</span>
              <span class="team">{{ match.awayTeam }}</span>
            </div>
            <div class="match-date">
              📅 {{ match.date || match.fixtureDate || 'Fecha no disponible' }}
            </div>
          </li>
        </ul>
      </div>

      <ng-template #empty>
        <div class="content-card empty-state">
          <div class="empty-icon">⏱️</div>
          <p class="empty-text">No se encontraron partidos.</p>
        </div>
      </ng-template>
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

    .content-card {
      padding: 2rem;
      background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
      border: 1px solid rgba(6, 182, 212, 0.15);
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    }

    .matches-list {
      list-style: none;
      padding: 0;
    }

    .match-item {
      padding: 1.5rem;
      margin-bottom: 1rem;
      background: rgba(6, 182, 212, 0.05);
      border: 1px solid rgba(6, 182, 212, 0.2);
      border-radius: 12px;
      transition: all 0.3s ease;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 2rem;
    }

    .match-item:last-child {
      margin-bottom: 0;
    }

    .match-item:hover {
      background: rgba(6, 182, 212, 0.1);
      border-color: rgba(6, 182, 212, 0.4);
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(6, 182, 212, 0.15);
    }

    .match-teams {
      display: flex;
      align-items: center;
      gap: 1rem;
      flex: 1;
      font-weight: 600;
    }

    .team {
      color: #0ea5e9;
      font-size: 1rem;
    }

    .vs {
      color: #64748b;
      font-size: 0.85rem;
      font-weight: 400;
    }

    .match-date {
      color: #cbd5e1;
      font-size: 0.9rem;
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

      .match-item {
        padding: 1rem;
        flex-direction: column;
        gap: 0.75rem;
        text-align: center;
      }

      .match-teams {
        width: 100%;
      }
    }
    `]
})
export class MatchListComponent implements OnInit {
  matches: any[] = [];

  ngOnInit() {
    ApiService.getMatches()
      .then((response) => (this.matches = response.data || []))
      .catch(() => (this.matches = []));
  }
}
