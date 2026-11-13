import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthUser } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/auth';
  private readonly tokenKey = 'solotrail_token';
  private readonly userKey = 'solotrail_user';

  register(data: { name: string; email: string; password: string }): Observable<string> {
    return this.http.post(`${this.baseUrl}/register`, data, { responseType: 'text' });
  }
  login(data: { email: string; password: string }): Observable<string> {
    return this.http.post(`${this.baseUrl}/login`, data, { responseType: 'text' }).pipe(tap(token => this.setSession(token)));
  }
  private setSession(token: string): void {
    const cleanToken = token.replace(/^"|"$/g, '');
    localStorage.setItem(this.tokenKey, cleanToken);
    const user = this.decodeUser(cleanToken);
    if (user) localStorage.setItem(this.userKey, JSON.stringify(user));
  }
  private decodeUser(token: string): AuthUser | null {
    try {
      const payload = JSON.parse(atob(token.split('.')[1])) as Record<string, unknown>;
      return { id: typeof payload['userId'] === 'number' ? payload['userId'] : typeof payload['id'] === 'number' ? payload['id'] : undefined, email: typeof payload['sub'] === 'string' ? payload['sub'] : undefined };
    } catch { return null; }
  }
  token(): string | null { return localStorage.getItem(this.tokenKey); }
  user(): AuthUser | null {
    const value = localStorage.getItem(this.userKey);
    return value ? JSON.parse(value) as AuthUser : null;
  }
  isLoggedIn(): boolean { return Boolean(this.token()); }
  logout(): void { localStorage.removeItem(this.tokenKey); localStorage.removeItem(this.userKey); }
}
