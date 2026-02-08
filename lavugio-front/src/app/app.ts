import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NotificationsComponent } from '@app/shared/components/notifications/notifications.component';
import { MessageBox } from "./shared/components/message-box/message-box";
import { LiveSupportButtonComponent } from "./shared/components/live-support-button/live-support-button";
import { AuthService } from './core/services/auth-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NotificationsComponent, MessageBox, LiveSupportButtonComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('lavugio-front');

  authService = inject(AuthService);

  isAuthenticated = false;

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated(); // ili isAdmin() / bilo koja logika
  }
}
