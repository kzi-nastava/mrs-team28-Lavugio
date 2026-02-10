import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { environment } from '@environments/environment';
import { ChatService } from './chat-service';
import { WebSocketService } from './web-socket-service';

export interface RegistrationRequest {
  email: string;
  password: string;
  name: string;
  lastName: string;
  phoneNumber: string;
  address: string;
}

export interface LoginRequest {
  email: string;
  password: string;
  longitude?: number;
  latitude?: number;
}

export interface LoginResponse {
  token: string;
  userId: number;
  email: string;
  name: string;
  role: string;
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.BACKEND_URL}/api/regularUsers`;

  private webSocketService = inject(WebSocketService);
  
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  isAuthenticatedSignal = signal<boolean>(!!localStorage.getItem('authToken'));


  private currentUserSubject = new BehaviorSubject<LoginResponse | null>(
    this.getStoredUser()
  );
  public currentUser$ = this.currentUserSubject.asObservable();

  register(data: RegistrationRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  registerWithFile(formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, formData);
  }

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, data);
  }

  verifyEmail(token: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/verify-email`, { token });
  }

  storeToken(token: string, user: LoginResponse): void {
    localStorage.setItem('authToken', token);
    localStorage.setItem('currentUser', JSON.stringify(user));
    this.isAuthenticatedSignal.set(true);
    this.isAuthenticatedSubject.next(true);
    this.currentUserSubject.next(user);
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  getStoredUser(): LoginResponse | null {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
  }

  getUserId(): number | null {
    const user = this.getStoredUser();
    return user ? user.userId : null;
  }

  logout(): Observable<void> {
    const user = this.getStoredUser();
    if (user?.userId) {
      return new Observable(observer => {
        this.http.post(`${this.apiUrl}/logout/${user.userId}`, {}).subscribe({
          next: () => {
            this.clearAuthData();
            observer.next();
            observer.complete();
          },
          error: (error) => {
            if (error.status !== 403) {
              this.clearAuthData();
            }
            observer.error(error);
            observer.complete();
          }
        });
      });
    } else {
      this.clearAuthData();
      return new Observable(observer => {
        observer.next();
        observer.complete();
      });
    }
  }

  private clearAuthData(): void {
    this.webSocketService.disconnect();
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    this.isAuthenticatedSignal.set(false);
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('authToken');
  }

  isAuthenticated(): boolean {
    return this.hasToken();
  }

  getUserRole(): string | null {
    const user = this.getStoredUser();
    return user?.role || null;
  }

  isDriver(): boolean {
    return this.getUserRole() === 'DRIVER';
  }

  isRegularUser(): boolean {
    return this.getUserRole() === 'REGULAR_USER';
  }

  isAdmin(): boolean {
    return this.getUserRole() === 'ADMIN';
  }
}
