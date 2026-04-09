import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Album, Lamina, LaminaUsuario, Paquete, Intercambio, ApiResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class AlbumService {
  private readonly BASE = `${environment.apiUrl}/album`;

  constructor(private http: HttpClient) {}

  getAlbumDelUsuario(): Observable<ApiResponse<Album>> {
    return this.http.get<ApiResponse<Album>>(`${this.BASE}/mi-album`);
  }

  getLaminasDelUsuario(): Observable<ApiResponse<LaminaUsuario[]>> {
    return this.http.get<ApiResponse<LaminaUsuario[]>>(`${this.BASE}/mis-laminas`);
  }

  getLaminasRepetidas(): Observable<ApiResponse<LaminaUsuario[]>> {
    return this.http.get<ApiResponse<LaminaUsuario[]>>(`${this.BASE}/repetidas`);
  }

  getPaquetesPendientes(): Observable<ApiResponse<Paquete[]>> {
    return this.http.get<ApiResponse<Paquete[]>>(`${this.BASE}/paquetes`);
  }

  abrirPaquete(paqueteId: string): Observable<ApiResponse<Lamina[]>> {
    return this.http.post<ApiResponse<Lamina[]>>(`${this.BASE}/paquetes/${paqueteId}/abrir`, {});
  }

  solicitarIntercambio(intercambio: Partial<Intercambio>): Observable<ApiResponse<Intercambio>> {
    return this.http.post<ApiResponse<Intercambio>>(`${this.BASE}/intercambios`, intercambio);
  }

  responderIntercambio(intercambioId: string, aceptar: boolean): Observable<ApiResponse<Intercambio>> {
    return this.http.put<ApiResponse<Intercambio>>(
      `${this.BASE}/intercambios/${intercambioId}`,
      { aceptar }
    );
  }

  getIntercambiosPendientes(): Observable<ApiResponse<Intercambio[]>> {
    return this.http.get<ApiResponse<Intercambio[]>>(`${this.BASE}/intercambios/pendientes`);
  }

  aplicarCodigoPromocional(codigo: string): Observable<ApiResponse<Paquete>> {
    return this.http.post<ApiResponse<Paquete>>(`${this.BASE}/codigo-promo`, { codigo });
  }
}
