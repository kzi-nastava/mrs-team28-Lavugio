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

  connect(): void {
    if (this.client && this.client.active) {
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

    this.client.activate();
    this.isInitialized = true;
  }

  subscribe(destination: string, callback: (message: IMessage) => void): StompSubscription | null {
    if (!this.client || !this.client.connected) {
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
      this.isInitialized = false;
    }
  }

  isConnected(): boolean {
    return this.client?.connected ?? false;
  }
}
