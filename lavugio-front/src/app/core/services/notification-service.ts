import { Injectable, signal } from '@angular/core';

export interface Notification {
  id: string;
  message: string;
  type: 'error' | 'warning' | 'success' | 'info';
  duration?: number;
}

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  notifications = signal<Notification[]>([]);

  showNotification(message: string, type: 'error' | 'warning' | 'success' | 'info' = 'info', duration = 5000) {
    const id = Math.random().toString(36).substr(2, 9);
    const notification: Notification = { id, message, type, duration };
    
    this.notifications.update(n => [...n, notification]);

    if (duration > 0) {
      setTimeout(() => this.dismissNotification(id), duration);
    }
  }

  dismissNotification(id: string) {
    this.notifications.update(n => n.filter(notif => notif.id !== id));
  }

  showAuthRequired() {
    this.showNotification(
      'You need to log in to access this feature',
      'warning',
      5000
    );
  }

  showEmailVerificationRequired() {
    this.showNotification(
      'Please verify your email to access advanced features. Check your inbox for the verification link.',
      'warning',
      5000
    );
  }
}
