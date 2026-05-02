import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../services/api.service';
import { AuthService } from '../services/auth.service';

@Component({
  standalone: true,
  selector: 'community-list',
  imports: [CommonModule],
  template: `
    <section class="page-card">
      <h2>Comunidades</h2>
      <button (click)="loadCommunities()">Cargar mis comunidades</button>
      <div *ngIf="loading">Cargando comunidades...</div>
      <div *ngIf="communities?.length">
        <ul>
          <li *ngFor="let community of communities">
            <strong>{{ community.name }}</strong>
            <span>{{ community.description }}</span>
          </li>
        </ul>
      </div>
      <p *ngIf="!loading && !communities?.length">Inicia sesión para ver tus comunidades.</p>
    </section>
  `,
  styles: [
    `.page-card { padding: 24px; background: white; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,.08); }
    button { margin-bottom: 16px; padding: 10px 16px; border: none; border-radius: 10px; background: #0288d1; color: white; cursor: pointer; }
    ul { list-style: none; padding: 0; }
    li { padding: 10px 0; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; gap: 12px; }
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
