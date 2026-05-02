import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';

@Component({
  standalone: true,
  selector: 'team-list',
  imports: [CommonModule],
  template: `
    <section class="page-card">
      <h2>Equipos</h2>
      <div *ngIf="teams?.length; else empty">
        <ul>
          <li *ngFor="let team of teams">
            <strong>{{ team.name }}</strong>
            <span>{{ team.groupName ? 'Grupo ' + team.groupName : '' }}</span>
          </li>
        </ul>
      </div>
      <ng-template #empty>
        <p>No se encontraron equipos.</p>
      </ng-template>
    </section>
  `,
  styles: [
    `.page-card { padding: 24px; background: white; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,.08); }
    ul { list-style: none; padding: 0; }
    li { padding: 12px 0; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; }
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
