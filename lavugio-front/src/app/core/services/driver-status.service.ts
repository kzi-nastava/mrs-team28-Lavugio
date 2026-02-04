import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from '@environments/environment';
import { AuthService } from './auth-service';

export interface DriverStatusResponse {
  message: string;
  active?: boolean;
  pending?: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class DriverStatusService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.BACKEND_URL}/api/drivers`;
  private authService = inject(AuthService);
  
  private driverStatusSubject = new BehaviorSubject<boolean | null>(null);
  public driverStatus$ = this.driverStatusSubject.asObservable();

  setDriverStatus(driverId: number, active: boolean): Observable<DriverStatusResponse> {
    
    return this.http.post<DriverStatusResponse>(
      `${this.apiUrl}/${driverId}/status`,
      { active }
    ).pipe(
      tap(response => {
        if (response.active !== undefined) {
          this.driverStatusSubject.next(response.active);
        }
      })
    );
  }

  getDriverStatus(driverId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${driverId}`);
  }

  isDriverActive(): Observable<boolean> {
    const userId = this.authService.getUserId();
    
    if (!userId) {
      return of(false);
    }

    return this.getDriverStatus(userId).pipe(
      map(driver => !!driver.active),
      catchError(() => of(false))
    );
  }

  updateLocalStatus(active: boolean): void {
    this.driverStatusSubject.next(active);
  }

  clearStatus(): void {
    this.driverStatusSubject.next(null);
  }
}
