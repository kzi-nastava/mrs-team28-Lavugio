import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from '@environments/environment';
import { AuthService } from '@app/core/services/auth-service';
import { Filters } from '@app/features/rides-reports/rides-reports';

export interface DateRangePayload {
  startDate: string;
  endDate: string;
}

export interface RidesReportsChartDto {
  title: string;
  xAxisLabel: string;
  yAxisLabel: string;
  labels: string[];
  data: number[];
  sum: number;
  average: number;
}

export interface RidesReportsResponse {
  charts: RidesReportsChartDto[];
}

@Injectable({
  providedIn: 'root',
})
export class RidesReportsService {
  private readonly baseUrl = `${environment.BACKEND_URL}/api/rides-reports`;
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  getReportsForCurrentUser(range: DateRangePayload): Observable<RidesReportsResponse> {
    if (this.authService.isDriver()) {
      return this.http.post<RidesReportsResponse>(`${this.baseUrl}/driver`, range);
    }

    if (this.authService.isRegularUser()) {
      return this.http.post<RidesReportsResponse>(`${this.baseUrl}/regular-user`, range);
    }

    return throwError(
      () => new Error('Rides reports are only available for drivers and regular users.'),
    );
  }

  getReportsForAdmin(filters: Filters): Observable<RidesReportsResponse> {
    return this.http.post<RidesReportsResponse>(`${this.baseUrl}/admin`, filters);
  }
}
