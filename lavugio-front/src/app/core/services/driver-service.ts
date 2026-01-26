import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DriverMarkerLocation } from '@app/shared/models/driverMarkerLocation';
import { Observable } from 'rxjs';
import { ScheduledRideDTO } from '@app/shared/models/scheduledRide';
import { environment } from 'environments/environment';

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
  
  getScheduledRides(driverId: number) {
    return this.http.get<ScheduledRideDTO[]>(`${this.mainPortUrl}/${driverId}/scheduled-rides`);
  }
}
