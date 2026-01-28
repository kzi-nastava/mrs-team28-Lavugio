import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '@environments/environment';

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

  updateLocalStatus(active: boolean): void {
    this.driverStatusSubject.next(active);
  }

  clearStatus(): void {
    this.driverStatusSubject.next(null);
  }
}
