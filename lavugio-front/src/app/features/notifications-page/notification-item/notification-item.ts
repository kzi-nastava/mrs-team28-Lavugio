import { CommonModule } from '@angular/common';
import { Component, input, model } from '@angular/core';
import { NotificationModel } from '@app/shared/models/notification';

@Component({
  selector: 'app-notification-item',
  imports: [CommonModule],
  templateUrl: './notification-item.html',
  styleUrl: './notification-item.css',
})
export class NotificationItem {
  notification = input.required<NotificationModel>()
}
