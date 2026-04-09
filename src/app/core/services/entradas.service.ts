import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Entrada, ApiResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class EntradasService {
  private readonly BASE = `${environment.apiUrl}/entradas`;

  constructor(private http: HttpClient) {}

  getMisEntradas(): Observable<ApiResponse<Entrada[]>> {
    return this.http.get<ApiResponse<Entrada[]>>(`${this.BASE}/mis-entradas`);
  }

  getEntradaPorId(id: string): Observable<ApiResponse<Entrada>> {
    return this.http.get<ApiResponse<Entrada>>(`${this.BASE}/${id}`);
  }

  reservarEntrada(partidoId: string, sector: string): Observable<ApiResponse<Entrada>> {
    return this.http.post<ApiResponse<Entrada>>(`${this.BASE}/reservar`, { partidoId, sector });
  }

  confirmarPago(entradaId: string, paymentMethodId: string): Observable<ApiResponse<Entrada>> {
    return this.http.post<ApiResponse<Entrada>>(`${this.BASE}/${entradaId}/pagar`, { paymentMethodId });
  }

  transferirEntrada(entradaId: string, emailDestinatario: string): Observable<ApiResponse<Entrada>> {
    return this.http.post<ApiResponse<Entrada>>(`${this.BASE}/${entradaId}/transferir`, { emailDestinatario });
  }

  solicitarReembolso(entradaId: string, motivo: string): Observable<ApiResponse<Entrada>> {
    return this.http.post<ApiResponse<Entrada>>(`${this.BASE}/${entradaId}/reembolso`, { motivo });
  }

  getHistorialEstados(entradaId: string): Observable<ApiResponse<Entrada>> {
    return this.http.get<ApiResponse<Entrada>>(`${this.BASE}/${entradaId}/historial`);
  }
}
