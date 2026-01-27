import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { DriverMarkerLocation } from '@app/shared/models/driverMarkerLocation';
import { Observable } from 'rxjs';
import { ScheduledRideDTO } from '@app/shared/models/ride/scheduledRide';
import { environment } from 'environments/environment';
import { RideHistoryDriverPagingModel } from '@app/shared/models/ride/rideHistoryDriverPagingModel';
import { RideHistoryDriverDetailedModel } from '@app/shared/models/ride/rideHistoryDriverDetailed';

@Injectable({
  providedIn: 'root',
})
export class DriverService {
  private mainPortUrl = environment.BACKEND_URL + '/api/drivers';

  http = inject(HttpClient);
  getDriverLocations(): Observable<DriverMarkerLocation[]> {
    return this.http.get<DriverMarkerLocation[]>(this.mainPortUrl + '/locations');
  }

  getDriverLocation(driverId: number): Observable<DriverMarkerLocation> {
    return this.http.get<DriverMarkerLocation>(`${this.mainPortUrl}/${driverId}/location`);
  }

  registerDriver(data: any): Observable<any> {
    return this.http.post<any>(`${this.mainPortUrl}/register`, data);
  }
  
  getScheduledRides() {
    return this.http.get<ScheduledRideDTO[]>(`${this.mainPortUrl}/scheduled-rides`);
  }

  getDriverRideHistory(
      page: number, 
      pageSize: number, 
      sorting: 'ASC' | 'DESC', 
      sortBy: 'START' | 'DEPARTURE' | 'DESTINATION',
      startDate: string,
      endDate: string){

    let params = new HttpParams().set('page', page)
      .set('pageSize', pageSize)
      .set('sorting', sorting)
      .set('sortBy', sortBy)
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<RideHistoryDriverPagingModel>(`${this.mainPortUrl}/history`, {params}) 
  }

  getDriverRideHistoryDetailed(rideId: number){
    return this.http.get<RideHistoryDriverDetailedModel>(`${this.mainPortUrl}/history/${rideId}`);
  }
}
