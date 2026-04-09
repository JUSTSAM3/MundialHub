import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Usuario, ApiResponse } from '../models';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  usuario: Usuario;
  expiresIn: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly TOKEN_KEY = 'mundial_token';
  private readonly USER_KEY  = 'mundial_user';

  // Signals de estado reactivo
  private _usuario = signal<Usuario | null>(this.loadUsuario());
  private _cargando = signal<boolean>(false);

  readonly usuario   = this._usuario.asReadonly();
  readonly cargando  = this._cargando.asReadonly();
  readonly estaAutenticado = computed(() => this._usuario() !== null);
  readonly esOperador      = computed(() => this._usuario()?.rol === 'operador' || this._usuario()?.rol === 'admin');
  readonly esSoporte       = computed(() => this._usuario()?.rol === 'soporte'  || this._usuario()?.rol === 'admin');

  constructor(private http: HttpClient, private router: Router) {}

  login(credenciales: LoginRequest): Observable<ApiResponse<AuthResponse>> {
    this._cargando.set(true);
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.apiUrl}/auth/login`, credenciales)
      .pipe(
        tap(resp => {
          if (resp.success && resp.data) {
            this.guardarSesion(resp.data);
          }
          this._cargando.set(false);
        }),
        catchError(err => {
          this._cargando.set(false);
          return throwError(() => err);
        })
      );
  }

  registro(datos: RegisterRequest): Observable<ApiResponse<AuthResponse>> {
    this._cargando.set(true);
    return this.http.post<ApiResponse<AuthResponse>>(`${environment.apiUrl}/auth/registro`, datos)
      .pipe(
        tap(resp => {
          if (resp.success && resp.data) {
            this.guardarSesion(resp.data);
          }
          this._cargando.set(false);
        }),
        catchError(err => {
          this._cargando.set(false);
          return throwError(() => err);
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this._usuario.set(null);
    this.router.navigate(['/auth/login']);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  private guardarSesion(auth: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, auth.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(auth.usuario));
    this._usuario.set(auth.usuario);
  }

  private loadUsuario(): Usuario | null {
    const raw = localStorage.getItem(this.USER_KEY);
    return raw ? JSON.parse(raw) as Usuario : null;
  }
}
