// chat.service.ts
import { Injectable, inject } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from 'environments/environment';
import { HttpClient } from '@angular/common/http';
import { ChatMessageModel } from '@app/shared/models/chatMessage';


@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private socketUrl = environment.BACKEND_URL + '/socket';
  private apiUrl = environment.BACKEND_URL + '/api/chat';
  private client: Client | undefined;
  private http = inject(HttpClient);
  
  private messagesSubject = new Subject<ChatMessageModel>();
  public messages$ = this.messagesSubject.asObservable();

  initializeWebSocketConnection() {
    let ws = new SockJS(this.socketUrl);
    this.client = new Client({
      webSocketFactory: () => ws,
      reconnectDelay: 5000,
    });
  }

  connectToChat(userId: number): Observable<ChatMessageModel> {
    if (!this.client) {
      this.initializeWebSocketConnection();
    }

    return new Observable<ChatMessageModel>((observer) => {
      this.client!.onConnect = () => {
        this.client!.subscribe(`/socket-publisher/chat/${userId}`, (message) => {
          let chatMessage: ChatMessageModel = JSON.parse(message.body);
          observer.next(chatMessage);
          this.messagesSubject.next(chatMessage);
        });
      };
      this.client!.activate();
    });
  }

  sendMessage(message: ChatMessageModel) {
    if (this.client && this.client.connected) {
      this.client.publish({
        destination: '/socket-subscriber/chat/send',
        body: JSON.stringify(message)
      });
      console.log(message);
    } else {
      console.error('WebSocket not connected');
    }
  }

  getChatHistory(userId: number): Observable<ChatMessageModel[]> {
    return this.http.get<ChatMessageModel[]>(`${this.apiUrl}/history/${userId}`);
  }

  closeConnection() {
    if (this.client) {
      this.client.deactivate();
    }
  }
}