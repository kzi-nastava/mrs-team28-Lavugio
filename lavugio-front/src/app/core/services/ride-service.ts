import { Injectable } from '@angular/core';
import { inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { RideOverviewModel } from '@app/shared/models/ride/rideOverview';
import { Observable, of } from 'rxjs';
import { WebSocketSubject, webSocket } from 'rxjs/webSocket';
import { RideOverviewUpdate } from '@app/shared/models/ride/rideOverviewUpdate';
import { Client, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from 'environments/environment';
import { RideReport } from '@app/shared/models/ride/rideReport';
import { RideReview } from '@app/shared/models/ride/rideReview';
import { ScheduledRideDTO } from '@app/shared/models/ride/scheduledRide';
import { RouteEstimateInfo } from '@app/shared/models/route/routeEstimateInfo';
import { RideEstimateRequest } from '@app/shared/models/ride/rideEstimateRequest';
import { ScheduleRideRequest } from '@app/shared/models/ride/scheduleRideRequest';
import { RideRequestDTO } from '@app/shared/models/ride/rideRequestDTO';
import { FinishRide } from '@app/shared/models/ride/finishRide';
import { RideHistoryDriverModel } from '@app/shared/models/ride/rideHistoryDriver';
import { RideHistoryDriverPagingModel } from '@app/shared/models/ride/rideHistoryDriverPagingModel';
@Injectable({
  providedIn: 'root',
})
export class RideService {
  mainPortUrl = environment.BACKEND_URL + '/api/rides';
  socketUrl = environment.BACKEND_URL + '/socket';
  client: Client | undefined;
  http = inject(HttpClient);

  constructor() {}

  getRideOverview(rideId: number): Observable<RideOverviewModel> {
    return this.http.get<RideOverviewModel>(`${this.mainPortUrl}/${rideId}/overview`);
  }

  getUpdatedRideOverview(rideId: number): Observable<RideOverviewModel> {
    return this.http.get<RideOverviewModel>(`${this.mainPortUrl}/${rideId}/overview/updated`);
  }

  initializeWebSocketConnection() {
    let ws = new SockJS(this.socketUrl);
    this.client = new Client({
      webSocketFactory: () => ws,
      reconnectDelay: 5000,
    });
  }

  listenToRideUpdates(rideId: number): Observable<RideOverviewUpdate> {
    if (!this.client) {
      this.initializeWebSocketConnection();
    }
    return new Observable<RideOverviewUpdate>((observer) => {
      this.client!.onConnect = () => {
        this.client!.subscribe(`/socket-publisher/rides/${rideId}/update`, (message) => {
          let rideUpdate: RideOverviewUpdate = JSON.parse(message.body);
          observer.next(rideUpdate);
        });
      };
      this.client!.activate();
    });
  }

  postStartRide(rideId: number): Observable<any> {
    return this.http.post<any>(`${this.mainPortUrl}/${rideId}/start`, {});
  }

  postRideReport(rideId: number, report: RideReport): Observable<RideReport> {
    return this.http.post<RideReport>(`${this.mainPortUrl}/report`, report);
  }

  postRideReview(rideId: number, review: RideReview): Observable<RideReview> {
    return this.http.post<RideReview>(`${this.mainPortUrl}/${rideId}/review`, review);
  }

  postRideFinish(finish: FinishRide): Observable<FinishRide>{
    return this.http.post<FinishRide>(`${this.mainPortUrl}/finish`, finish);
  }

  closeConnection(){
    if(this.client){
      this.client.deactivate();
    }
  }

  getPriceForRide(routeInfo: RideEstimateRequest): Observable<any> {
    return this.http.post<any>(`${this.mainPortUrl}/estimate-price`, routeInfo);
  }

  scheduleRide(scheduleRideRequest: ScheduleRideRequest): Observable<any> {
    return this.http.post<any>(`${this.mainPortUrl}/schedule`, scheduleRideRequest);
  }

  findRide(request: RideRequestDTO): Observable<any> {
    return this.http.post<any>(`${this.mainPortUrl}/find-ride`, request);
  }
 }
