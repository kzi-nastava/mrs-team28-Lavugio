import { Injectable } from '@angular/core';
import { inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RideOverviewModel } from '@app/shared/models/rideOverview';
import { Observable } from 'rxjs';
import { WebSocketSubject, webSocket } from 'rxjs/webSocket';
import { RideOverviewUpdate } from '@app/shared/models/rideOverviewUpdate';
import { Client, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from 'environments/environment';
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

  initializeWebSocketConnection(){
    let ws = new SockJS(this.socketUrl);
    this.client = new Client({
      webSocketFactory: () => ws,
      reconnectDelay: 5000,
    })
  }

  listenToRideUpdates(rideId: number): Observable<RideOverviewUpdate>{
    if(!this.client){
      this.initializeWebSocketConnection();
    }
    return new Observable<RideOverviewUpdate>(observer => {
      this.client!.onConnect = () => {
        this.client!.subscribe(`/socket-publisher/rides/${rideId}/update`, (message) => {
          let rideUpdate: RideOverviewUpdate = JSON.parse(message.body);
          observer.next(rideUpdate);
        });
      };
      this.client!.activate();
    });
  }

  closeConnection(){
    if(this.client){
      this.client.deactivate();
    }
  }
}
