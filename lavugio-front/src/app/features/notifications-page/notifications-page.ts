// notifications-page.ts
import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { NotificationModel } from '@app/shared/models/notification';
import { environment } from '@environments/environment';
import { NotificationItem } from './notification-item/notification-item';
import { BaseInfoPage } from "../base-info-page/base-info-page";
import { AuthService } from '@app/core/services/auth-service';
import { WebSocketService } from '@app/core/services/web-socket-service';
import { NotificationService } from '@app/core/services/notification-service';

@Component({
  selector: 'app-notifications-page',
  standalone: true,   // ako koristiš standalone komponente
  imports: [CommonModule, NotificationItem, BaseInfoPage],
  templateUrl: './notifications-page.html',
})
export class NotificationsPage implements OnInit{
  authService = inject(AuthService);
  notificationService = inject(NotificationService);
  private ws = inject(WebSocketService);

  notifications = signal<NotificationModel[]>([]);
  

  goToNotification(notification: NotificationModel) {
    if (notification.link) {
      window.location.href = `${environment.FRONT_URL}/${notification.link}`;
    }
  }

  ngOnInit() {

    this.notificationService.getNotifications().subscribe(data => {
      this.notifications.set(data);
    });
    const userId = this.authService.getUserId();

    this.ws.connect(() => {

      this.ws.subscribe(
        `/socket-publisher/notifications/${userId}`,
        (message) => {

          const notification: NotificationModel = JSON.parse(message.body);

          this.notifications.update(n => [
            notification,
            ...n
          ]);
        }
      );

    });
  }
}
