import { Routes } from '@angular/router';
import { LoginComponent } from './components/login.component';
import { RegisterComponent } from './components/register.component';
import { DashboardComponent } from './components/dashboard.component';
import { TeamListComponent } from './components/team-list.component';
import { MatchListComponent } from './components/match-list.component';
import { StickerAlbumComponent } from './components/sticker-album.component';
import { CommunityListComponent } from './components/community-list.component';
import { PollListComponent } from './components/poll-list.component';
import { AdminDashboardComponent } from './components/admin-dashboard.component';
import { AdminUsersComponent } from './components/admin-users.component';
import { AuthGuard } from './services/auth.guard';
import { AdminGuard } from './services/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'teams', component: TeamListComponent, canActivate: [AuthGuard] },
  { path: 'matches', component: MatchListComponent, canActivate: [AuthGuard] },
  { path: 'stickers', component: StickerAlbumComponent, canActivate: [AuthGuard] },
  { path: 'communities', component: CommunityListComponent, canActivate: [AuthGuard] },
  { path: 'polls', component: PollListComponent, canActivate: [AuthGuard] },
  
  // Admin routes
  { path: 'admin', component: AdminDashboardComponent, canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/users', component: AdminUsersComponent, canActivate: [AuthGuard, AdminGuard] },
  
  { path: '**', redirectTo: 'dashboard' }
];
