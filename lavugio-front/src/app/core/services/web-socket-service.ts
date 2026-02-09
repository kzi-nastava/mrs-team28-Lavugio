// websocket.service.ts
import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from 'environments/environment';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private client!: Client;
  private socketUrl = environment.BACKEND_URL + '/socket';
  private isInitialized = false;
  private isConnectedFlag = false;

  private pendingSubscriptions: { destination: string, callback: (message: IMessage) => void }[] = [];

  connect(onConnect?: () => void): void {
    if (this.client && this.client.active) {
      if (onConnect) onConnect();
      return;
    }

    const token = localStorage.getItem('jwt');

    this.client = new Client({
      webSocketFactory: () => new SockJS(this.socketUrl),
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      reconnectDelay: 5000,
      debug: () => {}
    });

    this.client.onConnect = () => {
      console.log('WebSocket connected');
      this.isConnectedFlag = true;

      // Pretplati sve što je čekalo dok se konekcija ne otvori
      this.pendingSubscriptions.forEach(sub => {
        this.client.subscribe(sub.destination, sub.callback);
      });
      this.pendingSubscriptions = [];

      if (onConnect) onConnect();
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP error', frame);
    };

    this.client.activate();
  }

  subscribe(destination: string, callback: (message: IMessage) => void): StompSubscription | null {

    if (!this.client) {
      this.pendingSubscriptions.push({ destination, callback });
      this.connect();
      return null;
    }

    if (!this.client.connected) {
      this.pendingSubscriptions.push({ destination, callback });
      return null;
    }

    return this.client.subscribe(destination, callback);
  }


  publish(destination: string, body: any): void {
    if (!this.client || !this.client.connected) {
      console.error('WebSocket not connected');
      return;
    }

    this.client.publish({
      destination,
      body: JSON.stringify(body)
    });
  }


  disconnect(): void {
    if (this.client) {
      this.client.deactivate();
      this.isConnectedFlag = false;
      this.pendingSubscriptions = [];
    }
  }

  isConnected(): boolean {
    return this.isConnectedFlag;
  }
}
