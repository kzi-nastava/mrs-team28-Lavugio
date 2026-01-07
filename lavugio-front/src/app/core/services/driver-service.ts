import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DriverMarkerLocation } from '@app/shared/models/driverMarkerLocation';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root',
})
export class DriverService {
  private mainPortUrl = 'http://localhost:8080/api/drivers'
  
  http = inject(HttpClient);
  getDriverLocations(): Observable<DriverMarkerLocation[]> {
    return this.http.get<DriverMarkerLocation[]>(this.mainPortUrl + '/locations');
  }
}
