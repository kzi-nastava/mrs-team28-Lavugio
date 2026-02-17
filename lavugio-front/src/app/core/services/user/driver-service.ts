import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { DriverMarkerLocation } from '@app/shared/models/driverMarkerLocation';
import { from, interval, Observable, Subscription, switchMap, tap } from 'rxjs';
import { ScheduledRideDTO } from '@app/shared/models/ride/scheduledRide';
import { environment } from 'environments/environment';
import { RideHistoryDriverPagingModel } from '@app/shared/models/ride/rideHistoryDriverPagingModel';
import { RideHistoryDriverDetailedModel } from '@app/shared/models/ride/rideHistoryDriverDetailed';
import { Coordinates } from '@app/shared/models/coordinates';
import { LocationService } from '../location-service';
import { DriverRegistration } from '@app/shared/models/user/driverRegistration';
import { DriverUpdateRequestDiffDTO, EditDriverProfileRequestDTO } from '@app/shared/models/user/editProfileDTO';

@Injectable({
  providedIn: 'root',
})
export class DriverService {
  private mainPortUrl = environment.BACKEND_URL + '/api/drivers';
  private locationService = inject(LocationService);
  private http = inject(HttpClient);
  
  private trackingSubscription?: Subscription;

  getDriverLocations(): Observable<DriverMarkerLocation[]> {
    return this.http.get<DriverMarkerLocation[]>(this.mainPortUrl + '/locations');
  }

  getDriverLocation(driverId: number): Observable<DriverMarkerLocation> {
    return this.http.get<DriverMarkerLocation>(`${this.mainPortUrl}/${driverId}/location`);
  }

  putDriverCoordinates(coords: Coordinates) {
    return this.http.put<DriverMarkerLocation>(`${this.mainPortUrl}/location`, coords);
  }

  registerDriver(data: DriverRegistration): Observable<any> {
    return this.http.post<any>(`${this.mainPortUrl}/register`, data);
  }

  sendEditRequest(updatedProfile: EditDriverProfileRequestDTO): Observable<any> {
    return this.http.post<any>(`${this.mainPortUrl}/edit-request`, updatedProfile);
  }

  getEditRequests(): Observable<DriverUpdateRequestDiffDTO[]> {
    return this.http.get<DriverUpdateRequestDiffDTO[]>(`${this.mainPortUrl}/edit-requests`);
  }

  approveEditRequest(requestId: number): Observable<void> {
    return this.http.post<void>(`${this.mainPortUrl}/edit-requests/${requestId}/approve`, {});
  }

  rejectEditRequest(requestId: number): Observable<void> {
    return this.http.post<void>(`${this.mainPortUrl}/edit-requests/${requestId}/reject`, {});
  }

  getScheduledRides(): Observable<ScheduledRideDTO[]> {
    return this.http.get<ScheduledRideDTO[]>(`${this.mainPortUrl}/scheduled-rides`);
  }

  getDriverRideHistory(
    page: number,
    pageSize: number,
    sorting: 'ASC' | 'DESC',
    sortBy: 'START' | 'DEPARTURE' | 'DESTINATION',
    startDate: string,
    endDate: string
  ): Observable<RideHistoryDriverPagingModel> {
    let params = new HttpParams()
      .set('page', page)
      .set('pageSize', pageSize)
      .set('sorting', sorting)
      .set('sortBy', sortBy)
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<RideHistoryDriverPagingModel>(`${this.mainPortUrl}/history`, { params });
  }

  getDriverRideHistoryDetailed(rideId: number): Observable<RideHistoryDriverDetailedModel> {
    return this.http.get<RideHistoryDriverDetailedModel>(`${this.mainPortUrl}/history/${rideId}`);
  }

  activateDriver(): Observable<any> {
    return from(this.locationService.getLocation()).pipe(
      switchMap((position) => {
        const coords: Coordinates = {
          longitude: position.coords.longitude,
          latitude: position.coords.latitude
        };
        return this.http.post<any>(`${this.mainPortUrl}/activate`, coords);
      }),
      tap(() => {
        this.startTracking();
      })
    );
  }

  deactivateDriver(): Observable<any> {
    this.stopTracking();
    return this.http.post<any>(`${this.mainPortUrl}/deactivate`, {});
  }

  getDriverActiveLast24Hours(): Observable<{ timeActive: string }> {
    return this.http.get<{ timeActive: string }>(`${this.mainPortUrl}/active-24h`);
  }

  startTracking(): void {
    if (this.trackingSubscription) {
      return; 
    }

    this.sendCurrentLocation();

    this.trackingSubscription = interval(3000).subscribe(() => {
      this.updateLocation();
    });
  }

  private updateLocation(): void {
    this.locationService.getLocation()
      .then((position) => {
        const coords: Coordinates = {
          longitude: position.coords.longitude,
          latitude: position.coords.latitude
        };

        this.putDriverCoordinates(coords).subscribe({
          next: () => console.log('Coordinates sent successfully'),
          error: (err) => console.error('Error while sending coordinates:', err)
        });
      })
      .catch((error) => {
        console.error('Error while getting coordinates', error);
      });
  }

  private sendCurrentLocation(): void {
    this.updateLocation();
  }

  stopTracking(): void {
    this.trackingSubscription?.unsubscribe();
    this.trackingSubscription = undefined;
  }
}