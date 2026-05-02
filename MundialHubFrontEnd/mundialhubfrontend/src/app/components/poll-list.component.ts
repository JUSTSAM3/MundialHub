import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'poll-list',
  imports: [CommonModule],
  template: `
    <section class="page-card">
      <h2>Pollas</h2>
      <button (click)="loadPolls()">Cargar mis pollas</button>
      <div *ngIf="loading">Cargando pollas...</div>
      <div *ngIf="polls?.length">
        <ul>
          <li *ngFor="let poll of polls">
            <strong>{{ poll.name }}</strong>
            <span>Comunidad: {{ poll.communityName || 'N/A' }}</span>
          </li>
        </ul>
      </div>
      <p *ngIf="!loading && !polls?.length">Inicia sesión para ver tus pollas.</p>
    </section>
  `,
  styles: [
    `.page-card { padding: 24px; background: white; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,.08); }
    button { margin-bottom: 16px; padding: 10px 16px; border: none; border-radius: 10px; background: #512da8; color: white; cursor: pointer; }
    ul { list-style: none; padding: 0; }
    li { padding: 10px 0; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; gap: 12px; }
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
