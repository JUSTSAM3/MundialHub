import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../services/api.service';

interface User {
  id?: number;
  name: string;
  username: string;
  email: string;
  role: 'ADMIN' | 'OPERATOR' | 'USER';
  status: 'ACTIVE' | 'PENDING_VERIFICATION' | 'INACTIVE';
  createdAt?: string;
  password?: string;
}

@Component({
  standalone: true,
  selector: 'admin-users',
  imports: [CommonModule, FormsModule],
  template: `
    <section class="admin-users">
      <div class="users-header">
        <h2>👥 Gestión de Usuarios</h2>
        <button (click)="showCreateForm = !showCreateForm" class="btn-create">
          {{ showCreateForm ? '✕ Cancelar' : '➕ Crear Usuario' }}
        </button>
      </div>

      <!-- Formulario de Creación -->
      <div *ngIf="showCreateForm" class="create-form-container">
        <div class="form-card">
          <h3>Crear Nuevo Usuario</h3>
          <form (submit)="createUser($event)" class="user-form">
            <div class="form-group">
              <label>Nombre Completo</label>
              <input type="text" [(ngModel)]="newUser.name" name="name" required placeholder="Juan Pérez" />
            </div>

            <div class="form-group">
              <label>Nombre de Usuario</label>
              <input type="text" [(ngModel)]="newUser.username" name="username" required placeholder="juanperez" />
            </div>

            <div class="form-group">
              <label>Email</label>
              <input type="email" [(ngModel)]="newUser.email" name="email" required placeholder="juan@example.com" />
            </div>

            <div class="form-group">
              <label>Contraseña</label>
              <input type="password" [(ngModel)]="newUser.password" name="password" required placeholder="Mínimo 6 caracteres" />
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Rol</label>
                <select [(ngModel)]="newUser.role" name="role" required>
                  <option value="USER">Usuario</option>
                  <option value="OPERATOR">Operador</option>
                  <option value="ADMIN">Administrador</option>
                </select>
              </div>

              <div class="form-group">
                <label>Estado</label>
                <select [(ngModel)]="newUser.status" name="status" required>
                  <option value="ACTIVE">Activo</option>
                  <option value="PENDING_VERIFICATION">Pendiente Verificación</option>
                  <option value="INACTIVE">Inactivo</option>
                </select>
              </div>
            </div>

            <button type="submit" class="btn-submit">Crear Usuario</button>
          </form>
          <p class="form-message" [class.success]="formMessage.includes('éxito')" [class.error]="formMessage.includes('Error')" *ngIf="formMessage">
            {{ formMessage }}
          </p>
        </div>
      </div>

      <!-- Búsqueda y Filtros -->
      <div class="filters-section">
        <input type="text" [(ngModel)]="searchTerm" placeholder="🔍 Buscar por nombre, email o usuario..." class="search-input" />
        <select [(ngModel)]="filterRole" class="filter-select">
          <option value="">Todos los Roles</option>
          <option value="ADMIN">Administrador</option>
          <option value="OPERATOR">Operador</option>
          <option value="USER">Usuario</option>
        </select>
        <select [(ngModel)]="filterStatus" class="filter-select">
          <option value="">Todos los Estados</option>
          <option value="ACTIVE">Activo</option>
          <option value="PENDING_VERIFICATION">Pendiente</option>
          <option value="INACTIVE">Inactivo</option>
        </select>
      </div>

      <!-- Lista de Usuarios -->
      <div class="users-list-container">
        <table class="users-table" *ngIf="filteredUsers.length > 0; else noUsers">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Usuario</th>
              <th>Email</th>
              <th>Rol</th>
              <th>Estado</th>
              <th>Creado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let user of filteredUsers" class="user-row">
              <td class="user-name">{{ user.name }}</td>
              <td class="user-username">@{{ user.username }}</td>
              <td class="user-email">{{ user.email }}</td>
              <td>
                <span class="role-badge" [ngClass]="user.role.toLowerCase()">
                  {{ getRoleLabel(user.role) }}
                </span>
              </td>
              <td>
                <span class="status-badge" [ngClass]="user.status.toLowerCase()">
                  {{ getStatusLabel(user.status) }}
                </span>
              </td>
              <td class="user-date">{{ user.createdAt | date: 'short' }}</td>
              <td class="actions">
                <button (click)="editUser(user)" class="btn-icon btn-edit" title="Editar">✏️</button>
                <button (click)="changeRole(user)" class="btn-icon btn-role" title="Cambiar Rol">🔐</button>
                <button (click)="toggleStatus(user)" class="btn-icon btn-toggle" title="Cambiar Estado">🔄</button>
                <button (click)="deleteUser(user)" class="btn-icon btn-delete" title="Eliminar">🗑️</button>
              </td>
            </tr>
          </tbody>
        </table>

        <ng-template #noUsers>
          <div class="empty-state">
            <div class="empty-icon">🔍</div>
            <p>No se encontraron usuarios</p>
          </div>
        </ng-template>
      </div>

      <!-- Modal de Edición -->
      <div *ngIf="editingUser" class="modal-overlay" (click)="editingUser = null">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <h3>Editar Usuario</h3>
          <form (submit)="saveEdit($event)" class="user-form">
            <div class="form-group">
              <label>Nombre Completo</label>
              <input type="text" [(ngModel)]="editingUser.name" name="name" required />
            </div>

            <div class="form-group">
              <label>Email</label>
              <input type="email" [(ngModel)]="editingUser.email" name="email" required />
            </div>

            <div class="form-group">
              <label>Rol</label>
              <select [(ngModel)]="editingUser.role" name="role" required>
                <option value="USER">Usuario</option>
                <option value="OPERATOR">Operador</option>
                <option value="ADMIN">Administrador</option>
              </select>
            </div>

            <div class="modal-actions">
              <button type="submit" class="btn-submit">Guardar Cambios</button>
              <button type="button" (click)="editingUser = null" class="btn-cancel">Cancelar</button>
            </div>
          </form>
        </div>
      </div>

      <!-- Estadísticas -->
      <div class="users-stats">
        <div class="stat-item">
          <span class="stat-label">Total de Usuarios</span>
          <span class="stat-value">{{ filteredUsers.length }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">Administradores</span>
          <span class="stat-value">{{ countByRole('ADMIN') }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">Operadores</span>
          <span class="stat-value">{{ countByRole('OPERATOR') }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">Usuarios Activos</span>
          <span class="stat-value">{{ countByStatus('ACTIVE') }}</span>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .admin-users {
      padding: 0;
    }

    .users-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
      padding-bottom: 1.5rem;
      border-bottom: 2px solid rgba(249, 115, 22, 0.2);
    }

    .users-header h2 {
      font-size: 1.8rem;
      font-weight: 800;
      color: #e2e8f0;
      margin: 0;
    }

    .btn-create {
      padding: 0.75rem 1.5rem;
      background: linear-gradient(135deg, #f97316, #ea580c);
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
    }

    .btn-create:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(249, 115, 22, 0.4);
    }

    /* Formulario de Creación */
    .create-form-container {
      margin-bottom: 2.5rem;
      animation: slideDown 0.3s ease;
    }

    @keyframes slideDown {
      from {
        opacity: 0;
        transform: translateY(-20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .form-card {
      padding: 2rem;
      background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
      border: 1px solid rgba(249, 115, 22, 0.2);
      border-radius: 14px;
      box-shadow: 0 10px 30px rgba(249, 115, 22, 0.15);
    }

    .form-card h3 {
      color: #f97316;
      font-size: 1.2rem;
      margin-bottom: 1.5rem;
    }

    .user-form {
      display: grid;
      gap: 1.25rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.25rem;
    }

    .form-group label {
      color: #e2e8f0;
      font-weight: 600;
      font-size: 0.9rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .form-group input,
    .form-group select {
      padding: 0.75rem;
      background: rgba(15, 23, 42, 0.6);
      border: 1.5px solid rgba(249, 115, 22, 0.3);
      border-radius: 8px;
      color: #e2e8f0;
      font-family: inherit;
      transition: all 0.3s ease;
    }

    .form-group input::placeholder {
      color: #64748b;
    }

    .form-group input:focus,
    .form-group select:focus {
      outline: none;
      border-color: #f97316;
      background: rgba(15, 23, 42, 0.8);
      box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
    }

    .btn-submit {
      padding: 0.9rem 1.5rem;
      background: linear-gradient(135deg, #f97316, #ea580c);
      color: white;
      border: none;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .btn-submit:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(249, 115, 22, 0.3);
    }

    .form-message {
      margin-top: 1rem;
      padding: 0.75rem;
      border-radius: 8px;
      text-align: center;
      font-weight: 600;
    }

    .form-message.success {
      background: rgba(16, 185, 129, 0.15);
      color: #10b981;
      border: 1px solid rgba(16, 185, 129, 0.3);
    }

    .form-message.error {
      background: rgba(239, 68, 68, 0.15);
      color: #ff6b6b;
      border: 1px solid rgba(239, 68, 68, 0.3);
    }

    /* Filtros */
    .filters-section {
      display: flex;
      gap: 1rem;
      margin-bottom: 2rem;
      flex-wrap: wrap;
    }

    .search-input,
    .filter-select {
      padding: 0.75rem 1rem;
      background: rgba(15, 23, 42, 0.6);
      border: 1.5px solid rgba(249, 115, 22, 0.2);
      border-radius: 8px;
      color: #e2e8f0;
      font-family: inherit;
      flex: 1;
      min-width: 200px;
      transition: all 0.3s ease;
    }

    .search-input::placeholder {
      color: #64748b;
    }

    .search-input:focus,
    .filter-select:focus {
      outline: none;
      border-color: #f97316;
      box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
    }

    /* Tabla de Usuarios */
    .users-list-container {
      margin-bottom: 2rem;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
      border: 1px solid rgba(249, 115, 22, 0.15);
    }

    .users-table {
      width: 100%;
      border-collapse: collapse;
      background: #1e293b;
    }

    .users-table thead {
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.15), rgba(245, 158, 11, 0.1));
      border-bottom: 2px solid rgba(249, 115, 22, 0.2);
    }

    .users-table th {
      padding: 1rem;
      color: #f97316;
      font-weight: 700;
      text-align: left;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      font-size: 0.85rem;
    }

    .user-row {
      border-bottom: 1px solid rgba(249, 115, 22, 0.1);
      transition: background 0.3s ease;
    }

    .user-row:hover {
      background: rgba(249, 115, 22, 0.08);
    }

    .users-table td {
      padding: 1rem;
      color: #cbd5e1;
      font-size: 0.9rem;
    }

    .user-name {
      font-weight: 600;
      color: #e2e8f0;
    }

    .user-username {
      color: #0ea5e9;
      font-family: monospace;
    }

    .user-date {
      color: #64748b;
      font-size: 0.85rem;
    }

    /* Roles y Estados */
    .role-badge,
    .status-badge {
      display: inline-block;
      padding: 0.4rem 0.75rem;
      border-radius: 6px;
      font-size: 0.8rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .role-badge.admin {
      background: rgba(249, 115, 22, 0.2);
      color: #f97316;
    }

    .role-badge.operator {
      background: rgba(14, 165, 233, 0.2);
      color: #0ea5e9;
    }

    .role-badge.user {
      background: rgba(6, 182, 212, 0.2);
      color: #06b6d4;
    }

    .status-badge.active {
      background: rgba(16, 185, 129, 0.2);
      color: #10b981;
    }

    .status-badge.pending_verification {
      background: rgba(245, 158, 11, 0.2);
      color: #f59e0b;
    }

    .status-badge.inactive {
      background: rgba(239, 68, 68, 0.2);
      color: #ff6b6b;
    }

    /* Acciones */
    .actions {
      display: flex;
      gap: 0.5rem;
      white-space: nowrap;
    }

    .btn-icon {
      width: 32px;
      height: 32px;
      border: none;
      border-radius: 6px;
      background: rgba(249, 115, 22, 0.15);
      color: #f97316;
      cursor: pointer;
      font-size: 0.9rem;
      transition: all 0.3s ease;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .btn-icon:hover {
      background: rgba(249, 115, 22, 0.3);
      transform: scale(1.1);
    }

    .btn-edit { background: rgba(14, 165, 233, 0.15); color: #0ea5e9; }
    .btn-edit:hover { background: rgba(14, 165, 233, 0.3); }

    .btn-role { background: rgba(139, 92, 246, 0.15); color: #8b5cf6; }
    .btn-role:hover { background: rgba(139, 92, 246, 0.3); }

    .btn-toggle { background: rgba(245, 158, 11, 0.15); color: #f59e0b; }
    .btn-toggle:hover { background: rgba(245, 158, 11, 0.3); }

    .btn-delete { background: rgba(239, 68, 68, 0.15); color: #ff6b6b; }
    .btn-delete:hover { background: rgba(239, 68, 68, 0.3); }

    /* Modal */
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.7);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 100;
      backdrop-filter: blur(4px);
      animation: fadeIn 0.3s ease;
    }

    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }

    .modal-content {
      background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
      border: 1px solid rgba(249, 115, 22, 0.2);
      border-radius: 14px;
      padding: 2rem;
      max-width: 500px;
      width: 90%;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
      animation: slideUp 0.3s ease;
    }

    @keyframes slideUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .modal-content h3 {
      color: #f97316;
      margin-bottom: 1.5rem;
    }

    .modal-actions {
      display: flex;
      gap: 1rem;
      margin-top: 1.5rem;
    }

    .btn-cancel {
      flex: 1;
      padding: 0.75rem 1.25rem;
      background: rgba(249, 115, 22, 0.15);
      border: 1px solid rgba(249, 115, 22, 0.3);
      color: #f97316;
      border-radius: 8px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
    }

    .btn-cancel:hover {
      background: rgba(249, 115, 22, 0.25);
    }

    /* Empty State */
    .empty-state {
      text-align: center;
      padding: 3rem 2rem;
      color: #cbd5e1;
    }

    .empty-icon {
      font-size: 3rem;
      margin-bottom: 1rem;
      opacity: 0.6;
    }

    /* Estadísticas */
    .users-stats {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1.5rem;
      padding: 2rem;
      background: linear-gradient(135deg, rgba(249, 115, 22, 0.08), rgba(245, 158, 11, 0.05));
      border: 1px solid rgba(249, 115, 22, 0.15);
      border-radius: 14px;
    }

    .stat-item {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
      text-align: center;
      padding: 1rem;
      background: rgba(249, 115, 22, 0.05);
      border-radius: 10px;
    }

    .stat-label {
      color: #cbd5e1;
      font-size: 0.85rem;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      font-weight: 500;
    }

    .stat-value {
      color: #f97316;
      font-size: 2rem;
      font-weight: 800;
    }

    @media (max-width: 1024px) {
      .form-row {
        grid-template-columns: 1fr;
      }

      .users-table {
        font-size: 0.85rem;
      }

      .users-table th,
      .users-table td {
        padding: 0.75rem 0.5rem;
      }

      .btn-icon {
        width: 28px;
        height: 28px;
      }
    }

    @media (max-width: 768px) {
      .users-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 1rem;
      }

      .filters-section {
        flex-direction: column;
      }

      .search-input,
      .filter-select {
        min-width: unset;
      }

      .users-table {
        font-size: 0.75rem;
      }

      .users-table th,
      .users-table td {
        padding: 0.5rem;
      }

      .user-name,
      .user-username,
      .user-email {
        display: block;
        word-break: break-word;
      }

      .actions {
        flex-direction: column;
      }

      .btn-icon {
        width: 100%;
      }
    }
  `]
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  newUser: User = {
    name: '',
    username: '',
    email: '',
    role: 'USER',
    status: 'ACTIVE',
    password: ''
  };
  editingUser: User | null = null;
  showCreateForm = false;
  searchTerm = '';
  filterRole = '';
  filterStatus = '';
  formMessage = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.api.getAllUsers().then((response: any) => {
      this.users = response.data || [];
      this.applyFilters();
    }).catch(error => {
      console.error('Error loading users:', error);
    });
  }

  applyFilters() {
    this.filteredUsers = this.users.filter(user => {
      const matchesSearch = !this.searchTerm || 
        user.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.username.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(this.searchTerm.toLowerCase());

      const matchesRole = !this.filterRole || user.role === this.filterRole;
      const matchesStatus = !this.filterStatus || user.status === this.filterStatus;

      return matchesSearch && matchesRole && matchesStatus;
    });
  }

  createUser(event: Event) {
    event.preventDefault();
    if (!this.validateForm()) {
      this.formMessage = 'Error: Por favor completa todos los campos correctamente';
      return;
    }

    this.api.createUser(this.newUser).then(() => {
      this.formMessage = 'Usuario creado con éxito';
      this.newUser = {
        name: '',
        username: '',
        email: '',
        role: 'USER',
        status: 'ACTIVE',
        password: ''
      };
      this.showCreateForm = false;
      setTimeout(() => this.loadUsers(), 1000);
    }).catch(error => {
      this.formMessage = `Error: ${error.response?.data || 'No se pudo crear el usuario'}`;
    });
  }

  editUser(user: User) {
    this.editingUser = { ...user };
  }

  saveEdit(event: Event) {
    event.preventDefault();
    if (!this.editingUser) return;

    this.api.updateUser(this.editingUser).then(() => {
      this.editingUser = null;
      this.loadUsers();
    }).catch(error => {
      console.error('Error updating user:', error);
    });
  }

  deleteUser(user: User) {
    if (confirm(`¿Estás seguro de que deseas eliminar a ${user.name}?`)) {
      this.api.deleteUser(user.id!).then(() => {
        this.loadUsers();
      }).catch(error => {
        console.error('Error deleting user:', error);
      });
    }
  }

  changeRole(user: User) {
    const roles = ['USER', 'OPERATOR', 'ADMIN'];
    const currentIndex = roles.indexOf(user.role);
    const nextRole = roles[(currentIndex + 1) % roles.length];
    user.role = nextRole as any;
    this.saveEdit({ preventDefault: () => {} } as any);
  }

  toggleStatus(user: User) {
    user.status = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    this.saveEdit({ preventDefault: () => {} } as any);
  }

  getRoleLabel(role: string): string {
    const labels: any = {
      'ADMIN': 'Administrador',
      'OPERATOR': 'Operador',
      'USER': 'Usuario'
    };
    return labels[role] || role;
  }

  getStatusLabel(status: string): string {
    const labels: any = {
      'ACTIVE': 'Activo',
      'PENDING_VERIFICATION': 'Pendiente',
      'INACTIVE': 'Inactivo'
    };
    return labels[status] || status;
  }

  countByRole(role: string): number {
    return this.filteredUsers.filter(u => u.role === role).length;
  }

  countByStatus(status: string): number {
    return this.filteredUsers.filter(u => u.status === status).length;
  }

  validateForm(): boolean {
    return this.newUser.name.trim() !== '' &&
           this.newUser.username.trim() !== '' &&
           this.newUser.email.includes('@') &&
           this.newUser.password.length >= 6;
  }
}
