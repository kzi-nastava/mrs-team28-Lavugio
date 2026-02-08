import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { WhiteSheetBackground } from '@app/shared/components/white-sheet-background/white-sheet-background';
import { Client, Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from 'environments/environment';
import { PriceDefinitionComponent } from "./price-definition/price-definition";
import { LiveSupportButtonComponent } from "@app/shared/components/live-support-button/live-support-button";

@Component({
  selector: 'app-admin-panel',
  imports: [Navbar, WhiteSheetBackground, CommonModule, PriceDefinitionComponent, LiveSupportButtonComponent],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css',
})
export class AdminPanel implements OnInit {

  private router = inject(Router);
  private socketUrl = environment.BACKEND_URL + '/socket';
  private client: Client | undefined;
  
  panicAlerts = signal<any[]>([]);
  hasUnreadPanic = signal<boolean>(false);
  showPanicPanel = signal<boolean>(false);
  isPriceDefinitionShown = signal<boolean>(false);

  ngOnInit() {
    this.initializePanicWebSocket();
  }

  private initializePanicWebSocket(): void {
    let ws = new SockJS(this.socketUrl);
    this.client = new Client({
      webSocketFactory: () => ws,
      reconnectDelay: 5000,
    });

    this.client.onConnect = () => {
      console.log('Connected to WebSocket for panic alerts');
      
      // Subscribe to panic alerts topic
      this.client!.subscribe('/socket-publisher/admin/panic', (message) => {
        const panicAlert = JSON.parse(message.body);
        console.log('Received panic alert:', panicAlert);
        
        // Add to alerts list
        this.panicAlerts.update(alerts => [panicAlert, ...alerts]);
        this.hasUnreadPanic.set(true);
        
        // Play alert sound
        this.playPanicAlertSound();
        
        // Show browser notification if permission granted
        this.showBrowserNotification(panicAlert);
      });
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP error:', frame);
    };

    this.client.activate();
  }

  private playPanicAlertSound(): void {
    try {
      const audioContext = new (window as any).AudioContext || (window as any).webkitAudioContext();
      const oscillator = audioContext.createOscillator();
      const gainNode = audioContext.createGain();
      
      oscillator.connect(gainNode);
      gainNode.connect(audioContext.destination);
      
      // Alarm pattern: alternating high tones
      for (let i = 0; i < 3; i++) {
        setTimeout(() => {
          oscillator.frequency.setValueAtTime(1000, audioContext.currentTime);
          gainNode.gain.setValueAtTime(0.5, audioContext.currentTime);
        }, i * 200);
      }
      
      gainNode.gain.setValueAtTime(0, audioContext.currentTime + 0.6);
      oscillator.start(audioContext.currentTime);
      oscillator.stop(audioContext.currentTime + 0.6);
    } catch (e) {
      console.warn('Could not play panic sound:', e);
    }
  }

  private showBrowserNotification(alert: any): void {
    if ('Notification' in window && Notification.permission === 'granted') {
      new Notification('🚨 PANIC ALERT', {
        body: `Passenger ${alert.passengerName} triggered panic in ride ${alert.rideId}`,
        icon: '🚨',
        tag: 'panic-alert',
        requireInteraction: true
      });
    }
  }

  openPanicAlerts(): void {
    this.showPanicPanel.set(!this.showPanicPanel());
    if (this.showPanicPanel()) {
      this.hasUnreadPanic.set(false);
    }
  }

  removePanicAlert(index: number): void {
    this.panicAlerts.update(alerts => alerts.filter((_, i) => i !== index));
  }

  clearAllPanicAlerts(): void {
    this.panicAlerts.set([]);
  }

  openDriverRegistration() {
    this.router.navigate(['/register-driver']);
  }

  openDriverUpdateRequest() {
    this.router.navigate(['/driver-update-requests']);
  }

  openBlockUser() {
    this.router.navigate(['/block-user']);
  }

  openReportsView() {
    alert('Not implemented yet')
  }

  showPriceDefinitionForm(){
    this.isPriceDefinitionShown.set(true);
  }

  hidePriceDefinitionForm(){
    this.isPriceDefinitionShown.set(false);
  }

}
