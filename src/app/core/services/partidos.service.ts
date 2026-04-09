import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, shareReplay, timer, switchMap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Partido, PaginatedResponse, ApiResponse } from '../models';

export interface FiltrosPartido {
  estado?: string;
  fase?: string;
  equipoId?: string;
  estadioId?: string;
  fecha?: string;
  pagina?: number;
  tamano?: number;
}

@Injectable({ providedIn: 'root' })
export class PartidosService {
  private readonly BASE = `${environment.apiUrl}/partidos`;

  // Cache del fixture completo (se refresca cada 5 min)
  private fixture$ = timer(0, 5 * 60 * 1000).pipe(
    switchMap(() => this.http.get<ApiResponse<Partido[]>>(`${this.BASE}/fixture`)),
    shareReplay(1)
  );

  constructor(private http: HttpClient) {}

  /** Obtiene todos los partidos (con caché) */
  getFixture(): Observable<ApiResponse<Partido[]>> {
    return this.fixture$;
  }

  /** Lista de partidos con filtros y paginación */
  getPartidos(filtros: FiltrosPartido = {}): Observable<PaginatedResponse<Partido>> {
    let params = new HttpParams();
    Object.entries(filtros).forEach(([k, v]) => {
      if (v !== undefined) params = params.set(k, String(v));
    });
    return this.http.get<PaginatedResponse<Partido>>(this.BASE, { params });
  }

  /** Detalle de un partido */
  getPartidoPorId(id: string): Observable<ApiResponse<Partido>> {
    return this.http.get<ApiResponse<Partido>>(`${this.BASE}/${id}`);
  }

  /** Partidos en vivo */
  getPartidosEnVivo(): Observable<ApiResponse<Partido[]>> {
    return this.http.get<ApiResponse<Partido[]>>(`${this.BASE}/en-vivo`);
  }

  /** Próximos partidos */
  getProximosPartidos(limite = 5): Observable<ApiResponse<Partido[]>> {
    return this.http.get<ApiResponse<Partido[]>>(`${this.BASE}/proximos`, {
      params: new HttpParams().set('limite', limite)
    });
  }

  /** Partidos de un equipo */
  getPartidosPorEquipo(equipoId: string): Observable<ApiResponse<Partido[]>> {
    return this.http.get<ApiResponse<Partido[]>>(`${this.BASE}?equipoId=${equipoId}`);
  }
}
