import { Component, OnInit, OnDestroy, Input, ViewChild, ElementRef, ChangeDetectorRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Message } from './message/message';
import { AuthService } from '@app/core/services/auth-service';
import { ChatService } from '@app/core/services/chat-service';
import { ChatMessageModel } from '@app/shared/models/chatMessage';
import { timeout } from 'rxjs';

@Component({
  selector: 'app-message-box',
  imports: [CommonModule, FormsModule, Message],
  templateUrl: './message-box.html',
  styleUrls: ['./message-box.css']
})
export class MessageBox implements OnInit, AfterViewInit {

  @Input() receiverId!: number;

  @ViewChild('scrollContainer') scrollContainer!: ElementRef;

  messages: ChatMessageModel[] = [];
  newMessage: string = '';
  userId: number = 0;

  constructor(
    private authService: AuthService,
    private chatService: ChatService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.userId = this.authService.getUserRole() === 'ADMIN' ? 0 : this.authService.getUserId()!;
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.loadChatHistory();
      this.connectToChat();
    });
  }

  trackByTimestamp(index: number, message: ChatMessageModel) {
    return message.timestamp.getTime();
  }

  private loadChatHistory() {
    this.chatService.getChatHistory(this.receiverId).subscribe({
      next: (history) => {
        this.messages = history.map(m => ({ ...m, timestamp: new Date(m.timestamp) }));
        this.cdr.detectChanges();
        setTimeout(() => this.scrollToBottom(), 0);
      },
      error: (err) => console.error('Error loading chat history:', err)
    });
  }

  private connectToChat() {
    this.chatService.connectToChat(this.receiverId).subscribe({
      next: (message) => {
        const exists = this.messages.some(
          m => m.senderId === message.senderId &&
               m.receiverId === message.receiverId &&
               m.text === message.text &&
               m.timestamp.getTime() === new Date(message.timestamp).getTime()
        );

        if (!exists) {
          this.messages.push({ ...message, timestamp: new Date(message.timestamp) });
          this.cdr.detectChanges();
          this.scrollToBottom();
        }
      },
      error: (err) => console.error('WebSocket error:', err)
    });
  }

  sendMessage() {
    if (!this.newMessage.trim()) return;

    const message: ChatMessageModel = {
      senderId: this.userId,
      receiverId: this.receiverId,
      text: this.newMessage.trim(),
      timestamp: new Date()
    };

    this.chatService.sendMessage(message);
    this.newMessage = '';
    this.cdr.detectChanges();
    this.scrollToBottom();
  }

  scrollToBottom() {
    setTimeout(() => {
      if (this.scrollContainer) {
        const el = this.scrollContainer.nativeElement;
        el.scrollTop = el.scrollHeight;
      }
    });
  }
}
