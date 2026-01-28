import { Router } from '@angular/router';
import { Component, computed, signal, inject, OnInit, OnDestroy, output, input } from '@angular/core';
import { DialogService } from '@app/core/services/dialog-service';
import { DriverService } from '@app/core/services/driver-service';
import { MapService } from '@app/core/services/map-service';
import { LocationService } from '@app/core/services/location-service';
import { Coordinates } from '@app/shared/models/coordinates';
import { RideOverviewModel } from '@app/shared/models/ride/rideOverview';
import { catchError, EMPTY, timeout } from 'rxjs';
import { RideService } from '@app/core/services/ride-service';
import { AuthService } from '@app/core/services/auth-service';

@Component({
  selector: 'app-ride-info',
  templateUrl: './ride-info.html',
  styleUrl: './ride-info.css',
})
export class RideInfo implements OnInit, OnDestroy {
  private router = inject(Router);
  navigateToCancelRide() {
    this.router.navigate([`/cancel-ride/${this.rideId()}`]);
  }
  // Dummy: Replace with real user/driver check
  isDriver(): boolean {
    // In real app, check auth/user service
    return true;
  }

  onStopRideClick() {
    this.dialogService.open(
      'Stop Ride',
      'Are you sure you want to stop the ride here? The destination will be updated and the price recalculated.',
      true
    );
    // On confirmation, call RideService.stopRide(this.rideId, this.driverLocation())
  }
  
  // Inputs from parent
  rideId = input.required<number>();
  rideOverview = input<RideOverviewModel | null>(null);
  isReported = input<boolean>(false);
  isReviewed = input<boolean>(false);
  canRateRide = input<boolean>(true);
  
  private rideService = inject(RideService);
  private authService = inject(AuthService);
  private nowIntervalId: any;
  private now = signal(new Date());
  private driverService = inject(DriverService);
  private mapService = inject(MapService);
  private dialogService = inject(DialogService);
  private locationService = inject(LocationService);
  
  isPanicLoading = signal(false);
  hasPanicBeenTriggered = signal(false);
  
  Math = Math;

  rideCancelled = output();
  
  // Outputs
  reportClicked = output();
  onPanicClick() {
    // Don't allow panic if already triggered
    if (this.hasPanicBeenTriggered()) {
      alert('Panic alert has already been sent for this ride.');
      return;
    }
    
    this.dialogService.openConfirm(
      'Panic confirmation',
      'Are you sure you want to send a panic alert? This will notify the administrators and mark your vehicle as in danger.'
    ).subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.triggerPanic();
      }
    });
  }

  private triggerPanic(): void {
    this.isPanicLoading.set(true);
    
    // Try to get current location first (request it fresh)
    this.locationService.getLocation()
      .then(position => {
        const currentLocation = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        };
        
        const rideOverview = this.rideOverview();
        
        if (!rideOverview) {
          alert('Unable to get ride information. Please try again.');
          this.isPanicLoading.set(false);
          return;
        }

        // Send panic with the fresh location
        this.sendPanicAlert(currentLocation, rideOverview);
      })
      .catch(error => {
        this.isPanicLoading.set(false);
        console.error('Location error:', error);
        alert(
          '❌ Location Required\n\n' +
          'Unable to get your location. Panic alerts require location data.\n\n' +
          'Error: ' + (error.message || error) + '\n\n' +
          'To enable location:\n' +
          '1. Click the lock icon (🔒) in the address bar\n' +
          '2. Find "Location" permission\n' +
          '3. Change it to "Allow"\n' +
          '4. Refresh the page and try again'
        );
      });
  }

  private sendPanicAlert(currentLocation: any, rideOverview: any): void {
    const currentUser = this.authService.getStoredUser();
    
    // Create panic notification payload
    const panicAlert = {
      rideId: this.rideId(),
      passengerId: currentUser?.userId || 0,
      passengerName: currentUser?.name || 'Passenger',
      location: {
        latitude: currentLocation.latitude,
        longitude: currentLocation.longitude
      },
      message: 'EMERGENCY: Passenger triggered panic button during active ride',
      timestamp: new Date(),
      vehicleType: 'Vehicle',
      vehicleLicensePlate: 'N/A',
      driverName: rideOverview.driverName || 'Driver'
    };

    // Call backend panic endpoint
    this.rideService.triggerPanic(this.rideId(), panicAlert).subscribe({
      next: (response: any) => {
        this.isPanicLoading.set(false);
        this.hasPanicBeenTriggered.set(true); // Mark as triggered
        // Play alert sound
        this.playPanicSound();
        // Show success message
        alert('PANIC ALERT SENT! Administrators have been notified. The ride has been stopped.');
        console.log('Panic activated successfully:', response);
        
        // Navigate back to active rides and force reload
        this.router.navigate(['/active-rides']).then(() => {
          window.location.reload();
        });
      },
      error: (error: any) => {
        this.isPanicLoading.set(false);
        console.error('Error triggering panic:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.error);
        
        if (error.status === 400 && error.error?.includes?.('Panic can only be triggered on active rides')) {
          alert('This ride is no longer active. Panic cannot be triggered.');
        } else {
          alert('Failed to send panic alert. Please try again.');
        }
      }
    });
  }

  private playPanicSound(): void {
    // Play alert sound - using Web Audio API or HTML5 audio
    try {
      const audioContext = new (window as any).AudioContext || (window as any).webkitAudioContext();
      const oscillator = audioContext.createOscillator();
      const gainNode = audioContext.createGain();
      
      oscillator.connect(gainNode);
      gainNode.connect(audioContext.destination);
      
      // Alarm frequency pattern: alternating high and low tones
      oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
      oscillator.frequency.setValueAtTime(1200, audioContext.currentTime + 0.1);
      oscillator.frequency.setValueAtTime(800, audioContext.currentTime + 0.2);
      
      gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
      gainNode.gain.setValueAtTime(0, audioContext.currentTime + 0.3);
      
      oscillator.start(audioContext.currentTime);
      oscillator.stop(audioContext.currentTime + 0.3);
    } catch (e) {
      console.warn('Could not play panic sound:', e);
    }
  }
  reviewClicked = output();
  
  // Internal state
  userLocation = signal<Coordinates | null>(null);
  duration = signal<number>(0);
  
  // Computed values
  departureTime = computed(() => {
    const depTime = this.rideOverview()?.departureTime;
    if (!depTime) return null;
    
    const date = typeof depTime === 'string' ? new Date(depTime) : depTime;
    return this.formatDateTime(date);
  });
  
  arrivalTime = computed(() => {
    const arrTime = this.rideOverview()?.arrivalTime;
    if (!arrTime) return null;
    
    const date = typeof arrTime === 'string' ? new Date(arrTime) : arrTime;
    return this.formatDateTime(date);
  });
  
  rideStatus = computed(() => this.rideOverview()?.status || 'Loading...');
  
  checkpoints = computed(() => this.rideOverview()?.checkpoints || []);

  timeElapsed = computed(() => {
    const overview = this.rideOverview();
    if (!overview || !overview.departureTime) return 0;

    // Debug - privremeno dodaj ovo
    console.log('Computing timeElapsed:');
    console.log('Departure raw:', overview.departureTime, typeof overview.departureTime);
    console.log('Arrival raw:', overview.arrivalTime, typeof overview.arrivalTime);

    const departureDate = typeof overview.departureTime === 'string' 
      ? new Date(overview.departureTime) 
      : overview.departureTime;

    console.log('Departure Date:', departureDate, departureDate.getTime());

    if (overview.arrivalTime) {
      const arrivalDate = typeof overview.arrivalTime === 'string'
        ? new Date(overview.arrivalTime)
        : overview.arrivalTime;
      
      console.log('Arrival Date:', arrivalDate, arrivalDate.getTime());
      
      const diffMs = arrivalDate.getTime() - departureDate.getTime();
      console.log('Diff ms:', diffMs, 'Minutes:', Math.ceil(diffMs / 60000));
      return Math.ceil(diffMs / 60000);
    }

    const diffMs = this.now().getTime() - departureDate.getTime();
    console.log('Now diff ms:', diffMs, 'Minutes:', Math.ceil(diffMs / 60000));
    return Math.ceil(diffMs / 60000);
  });

  ngOnInit() {
    this.createOneMinuteInterval();
    
    // Initialize panic state from ride overview
    const overview = this.rideOverview();
    if (overview && (overview as any).hasPanic) {
      this.hasPanicBeenTriggered.set(true);
    }
  }

  createOneMinuteInterval() {
    this.nowIntervalId = setInterval(() => {
      this.now.set(new Date());
    }, 60000);
  }

  calculateDuration(): void {
    const checkpoints = this.checkpoints();
    const driver = this.userLocation();

    if (!driver || !checkpoints || checkpoints.length === 0) {
      console.warn('No checkpoints or driver location available for duration calculation.');
      return;
    }

    const finish = [driver, checkpoints[checkpoints.length - 1]];

    this.mapService
      .getRoute(
        finish.map(cp => ({ lat: cp.latitude, lon: cp.longitude }))
      )
      .subscribe(routeData => {
        const durationInSeconds = routeData.routes[0].duration;
        console.log('Route duration (seconds):', durationInSeconds);
        this.duration.set(Math.ceil(durationInSeconds / 60));
      });
  }

  formatDateTime(date: Date | null | undefined): string {
    if (!date) return "Loading...";
    console.log(date);
    const pad = (n: number) => n.toString().padStart(2, '0');

    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
    const day = pad(date.getDate());
    const month = pad(date.getMonth() + 1); 
    const year = date.getFullYear();

    return `${hours}:${minutes} ${day}.${month}.${year}`;
  }

  cancelRide() {
    const overview = this.rideOverview();
    if (!overview || !overview.departureTime) {
      alert('Unable to cancel ride: missing ride information');
      return;
    }
    
    // Check if cancellation is allowed (10 minutes before start)
    const now = new Date();
    const startTime = new Date(overview.departureTime);
    const minutesUntilStart = (startTime.getTime() - now.getTime()) / (1000 * 60);
    
    if (minutesUntilStart < 10) {
      alert('Cannot cancel ride less than 10 minutes before start time.');
      return;
    }
    
    // Confirm cancellation
    this.dialogService.openConfirm(
      'Cancel Ride',
      'Are you sure you want to cancel this ride? This action cannot be undone.'
    ).subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      
      this.executeCancelRide();
    });
  }

  private executeCancelRide(): void {
    const rideIdValue = this.rideId();
    this.rideService.cancelRideByPassenger(rideIdValue).subscribe({
      next: () => {
        console.log('✅ Ride canceled successfully:', rideIdValue);
        alert('✅ Ride canceled successfully.');
        this.rideCancelled.emit();
        // Navigate back to active rides
        this.router.navigate(['/active-rides']);
      },
      error: (err) => {
        console.error('❌ Failed to cancel ride:', err);
        const errorMessage = err.error?.error || err.message || 'Unknown error';
        alert('❌ Failed to cancel ride.\n\nError: ' + errorMessage);
      }
    });
  }

  ngOnDestroy() {
    clearInterval(this.nowIntervalId);
  }
}