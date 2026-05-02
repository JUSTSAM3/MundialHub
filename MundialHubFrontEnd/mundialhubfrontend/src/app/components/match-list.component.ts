import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';

@Component({
  standalone: true,
  selector: 'match-list',
  imports: [CommonModule],
  template: `
    <section class="page-card">
      <h2>Partidos</h2>
      <div *ngIf="matches?.length; else empty">
        <ul>
          <li *ngFor="let match of matches">
            <span>{{ match.homeTeam }} vs {{ match.awayTeam }}</span>
            <small>{{ match.date || match.fixtureDate || 'Fecha no disponible' }}</small>
          </li>
        </ul>
      </div>
      <ng-template #empty>
        <p>No se encontraron partidos.</p>
      </ng-template>
    </section>
  `,
  styles: [
    `.page-card { padding: 24px; background: white; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,.08); }
    ul { list-style: none; padding: 0; }
    li { padding: 12px 0; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: baseline; }
    small { color: #64748b; }
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
