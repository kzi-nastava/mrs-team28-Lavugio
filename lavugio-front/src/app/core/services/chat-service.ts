// chat.service.ts
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ChatMessageModel } from '@app/shared/models/chatMessage';
import { WebSocketService } from './web-socket-service';
import { environment } from 'environments/environment';
import { StompSubscription } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private apiUrl = environment.BACKEND_URL + '/api/chat';
  private http = inject(HttpClient);

  constructor(private wsService: WebSocketService) {}

  connectToChat(userId: number): Observable<ChatMessageModel> {

    this.wsService.connect();

    return new Observable<ChatMessageModel>((observer) => {

      const trySubscribe = () => {
        const sub = this.wsService.subscribe(
          `/socket-publisher/chat/${userId}`,
          (message) => {
            const chatMessage: ChatMessageModel = JSON.parse(message.body);
            observer.next(chatMessage);
          }
        );

        if (!sub) {
          setTimeout(trySubscribe, 200); // čekaj dok se ne poveže
        }

        return sub;
      };

      const subscription: StompSubscription | null = trySubscribe();

      return () => {
        subscription?.unsubscribe();
      };
    });
  }

  sendMessage(message: ChatMessageModel) {
    this.wsService.publish(
      '/socket-subscriber/chat/send',
      message
    );
  }

  getChatHistory(userId: number): Observable<ChatMessageModel[]> {
    return this.http.get<ChatMessageModel[]>(`${this.apiUrl}/history/${userId}`);
  }

  disconnect() {
    this.wsService.disconnect();
  }
}
