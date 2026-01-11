import { Injectable } from '@angular/core';
import { inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RideOverviewModel } from '@app/shared/models/rideOverview';
import { Observable } from 'rxjs';
import { WebSocketSubject, webSocket } from 'rxjs/webSocket';
import { RideOverviewUpdate } from '@app/shared/models/rideOverviewUpdate';
@Injectable({
  providedIn: 'root',
})
export class RideService {
  mainPortUrl = 'http://localhost:8080/api/rides';
  http = inject(HttpClient);

    private ws!: WebSocketSubject<RideOverviewUpdate>;

  constructor() {}

  listenUpdatedRide(rideId: number): Observable<RideOverviewUpdate> {
    if (!this.ws || this.ws.closed) {
      this.ws = webSocket<RideOverviewUpdate>(
        `ws://localhost:8080/api/topic/rides/${rideId}`
      );
    }

    return this.ws.asObservable();
  }

  closeConnection() {
    this.ws?.complete();
  }


  getRideOverview(rideId: number): Observable<RideOverviewModel> {
    return this.http.get<RideOverviewModel>(`${this.mainPortUrl}/${rideId}/overview`);
  }

  getUpdatedRideOverview(rideId: number): Observable<RideOverviewModel> {
    return this.http.get<RideOverviewModel>(`${this.mainPortUrl}/${rideId}/overview/updated`);
  }
}
