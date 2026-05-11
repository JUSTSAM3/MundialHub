import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { ApiService } from './api.service';
import SHA256 from 'crypto-js/sha256';

interface DecodedToken {
  sub: string;
  exp: number;
  iat: number;
  authorities?: string[];
  role?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private tokenKey = 'mundialhub_token';

  private hashPassword(password: string): string {
    // Hash twice as backend expects
    let hashed = SHA256(password).toString();
    hashed = SHA256(hashed).toString();
    return hashed;
  }

  async login(username: string, password: string) {
    const hashedPassword = this.hashPassword(password);
    return ApiService.login(username, hashedPassword).then((response) => {
      const token = response.data?.token || response.data?.jwt || response.data;
      if (token) {
        localStorage.setItem(this.tokenKey, token);
      }
      return response;
    });
  }

  async register(data: Record<string, unknown>) {
    const hashedData = {
      ...data,
      password: this.hashPassword(data['password'] as string)
    };
    return ApiService.register(hashedData);
  }

  logout() {
    if (typeof localStorage !== 'undefined') {
      localStorage.removeItem(this.tokenKey);
    }
  }

  getToken() {
    if (typeof localStorage === 'undefined') {
      return null;
    }
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated() {
    const token = this.getToken();
    if (!token) return false;
    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  getUserRole() {
    const token = this.getToken();
    if (!token) return null;
    try {
      const decoded = jwtDecode<DecodedToken>(token);
      return decoded.authorities || decoded.role || null;
    } catch {
      return null;
    }
  }
}
