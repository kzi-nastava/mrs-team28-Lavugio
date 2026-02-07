import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MessageBox } from '../message-box/message-box'; 
import { AuthService } from '@app/core/services/auth-service';
import { AdminChatComponent } from "../admin-chat/admin-chat";

@Component({
  selector: 'app-live-support-button',
  imports: [CommonModule, MessageBox, AdminChatComponent],
  templateUrl: './live-support-button.html', 
})
export class LiveSupportButtonComponent implements OnInit{
  authService = inject(AuthService);
  chatVisible = false;
  supportId = this.authService.getUserId();
  isAdmin = signal(false);

  ngOnInit(): void {
    this.isAdmin.set(this.authService.isAdmin());
  }

  toggleChat() {
    this.chatVisible = !this.chatVisible;
  }
}
