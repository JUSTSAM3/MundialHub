import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Notificacion, ApiResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class NotificacionesService {
  private readonly BASE = `${environment.apiUrl}/notificaciones`;

  // Conteo de no leídas
  readonly noLeidas = signal<number>(0);

  constructor(private http: HttpClient) {}

  getMisNotificaciones(): Observable<ApiResponse<Notificacion[]>> {
    return this.http.get<ApiResponse<Notificacion[]>>(this.BASE);
  }

  marcarLeida(id: string): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.BASE}/${id}/leer`, {});
  }

  marcarTodasLeidas(): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.BASE}/leer-todas`, {});
  }

  // Backoffice: enviar notificación masiva (operadores)
  enviarNotificacionMasiva(payload: {
    titulo: string;
    cuerpo: string;
    segmento: 'todos' | 'partido' | 'ciudad' | 'estadio';
    segmentoId?: string;
  }): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.BASE}/masiva`, payload);
  }

  actualizarNoLeidas(cantidad: number): void {
    this.noLeidas.set(cantidad);
  }
}
