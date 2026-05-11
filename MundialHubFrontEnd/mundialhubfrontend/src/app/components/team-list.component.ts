import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';

@Component({
  standalone: true,
  selector: 'team-list',
  imports: [CommonModule],
  template: `
    <section class="page-container">
      <div class="page-header">
        <h2>⚽ Equipos del Torneo</h2>
        <p class="page-subtitle">Conoce todos los equipos participantes</p>
      </div>

      <div *ngIf="teams?.length; else empty" class="content-card">
        <ul class="teams-list">
          <li *ngFor="let team of teams" class="team-item">
            <div class="team-info">
              <div class="team-icon">🏆</div>
              <div class="team-details">
                <strong class="team-name">{{ team.name }}</strong>
                <span class="team-group" *ngIf="team.groupName">Grupo {{ team.groupName }}</span>
              </div>
            </div>
          </li>
        </ul>
      </div>

      <ng-template #empty>
        <div class="content-card empty-state">
          <div class="empty-icon">📭</div>
          <p class="empty-text">No se encontraron equipos.</p>
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

    .teams-list {
      list-style: none;
      padding: 0;
    }

    .team-item {
      padding: 1.5rem;
      margin-bottom: 1rem;
      background: rgba(6, 182, 212, 0.05);
      border: 1px solid rgba(6, 182, 212, 0.2);
      border-radius: 12px;
      transition: all 0.3s ease;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .team-item:last-child {
      margin-bottom: 0;
    }

    .team-item:hover {
      background: rgba(6, 182, 212, 0.1);
      border-color: rgba(6, 182, 212, 0.4);
      transform: translateX(4px);
      box-shadow: 0 4px 12px rgba(6, 182, 212, 0.15);
    }

    .team-info {
      display: flex;
      align-items: center;
      gap: 1rem;
      flex: 1;
    }

    .team-icon {
      font-size: 1.75rem;
      min-width: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .team-details {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .team-name {
      color: #0ea5e9;
      font-weight: 700;
      font-size: 1rem;
    }

    .team-group {
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

      .team-item {
        padding: 1rem;
        flex-direction: column;
        text-align: center;
      }

      .team-info {
        width: 100%;
        flex-direction: column;
        gap: 0.75rem;
      }
    }
    `]
})
export class TeamListComponent implements OnInit {
  teams: any[] = [];

  ngOnInit() {
    ApiService.getTeams()
      .then((response) => (this.teams = response.data || []))
      .catch(() => (this.teams = []));
  }
}
