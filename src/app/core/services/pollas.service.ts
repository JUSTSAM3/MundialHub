import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Polla, Pronostico, MiembroPolla, ApiResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class PollasService {
  private readonly BASE = `${environment.apiUrl}/pollas`;

  constructor(private http: HttpClient) {}

  crearPolla(nombre: string): Observable<ApiResponse<Polla>> {
    return this.http.post<ApiResponse<Polla>>(this.BASE, { nombre });
  }

  getPollasDelUsuario(): Observable<ApiResponse<Polla[]>> {
    return this.http.get<ApiResponse<Polla[]>>(`${this.BASE}/mis-pollas`);
  }

  getPollaPorId(id: string): Observable<ApiResponse<Polla>> {
    return this.http.get<ApiResponse<Polla>>(`${this.BASE}/${id}`);
  }

  unirseConCodigo(codigo: string): Observable<ApiResponse<Polla>> {
    return this.http.post<ApiResponse<Polla>>(`${this.BASE}/unirse`, { codigo });
  }

  registrarPronostico(pollaId: string, pronostico: Partial<Pronostico>): Observable<ApiResponse<Pronostico>> {
    return this.http.post<ApiResponse<Pronostico>>(`${this.BASE}/${pollaId}/pronosticos`, pronostico);
  }

  getRanking(pollaId: string): Observable<ApiResponse<MiembroPolla[]>> {
    return this.http.get<ApiResponse<MiembroPolla[]>>(`${this.BASE}/${pollaId}/ranking`);
  }
}
