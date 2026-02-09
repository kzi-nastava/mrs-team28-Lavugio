import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';
import { RideHistoryAdminPagingModel, RideHistoryAdminDetailedModel } from '@app/shared/models/ride/rideHistoryAdmin';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private apiUrl = environment.BACKEND_URL + '/api';

  constructor(private http: HttpClient) {}

  getUserRideHistory(
    email: string,
    page: number,
    pageSize: number,
    sorting: 'ASC' | 'DESC',
    sortBy: 'START' | 'DEPARTURE' | 'DESTINATION' | 'PRICE' | 'CANCELLED' | 'PANIC',
    startDate: string,
    endDate: string
  ): Observable<RideHistoryAdminPagingModel> {
    let params = new HttpParams()
      .set('email', email)
      .set('page', page)
      .set('pageSize', pageSize)
      .set('sorting', sorting)
      .set('sortBy', sortBy)
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<RideHistoryAdminPagingModel>(`${this.apiUrl}/admin/user-history`, { params });
  }

  getRideHistoryDetailed(rideId: number): Observable<RideHistoryAdminDetailedModel> {
    return this.http.get<RideHistoryAdminDetailedModel>(`${this.apiUrl}/admin/user-history/${rideId}`);
  }
}
