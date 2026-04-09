import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

let correlationCounter = 0;

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth  = inject(AuthService);
  const token = auth.getToken();
  const correlationId = `req-${Date.now()}-${++correlationCounter}`;

  // Adjunta token y cabeceras de correlación
  const cloned = req.clone({
    setHeaders: {
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      'X-Correlation-ID': correlationId,
      'X-Client': 'mundial-2026-hub-web'
    }
  });

  // Log estructurado de salida
  console.log(JSON.stringify({
    timestamp: new Date().toISOString(),
    level: 'INFO',
    correlationId,
    tipo: 'HTTP_REQUEST',
    method: req.method,
    url: req.url
  }));

  return next(cloned).pipe(
    catchError((error: HttpErrorResponse) => {
      const router = inject(Router);

      console.error(JSON.stringify({
        timestamp: new Date().toISOString(),
        level: 'ERROR',
        correlationId,
        tipo: 'HTTP_ERROR',
        status: error.status,
        url: req.url,
        mensaje: error.message
      }));

      if (error.status === 401) {
        auth.logout();
      }

      return throwError(() => error);
    })
  );
};
