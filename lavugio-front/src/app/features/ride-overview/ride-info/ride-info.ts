import { Router } from '@angular/router';
import { Component, computed, signal, inject, OnInit, OnDestroy, output, input } from '@angular/core';
import { DialogService } from '@app/core/services/dialog-service';
import { DriverService } from '@app/core/services/driver-service';
import { MapService } from '@app/core/services/map-service';
import { Coordinates } from '@app/shared/models/coordinates';
import { RideOverviewModel } from '@app/shared/models/rideOverview';
import { catchError, EMPTY, timeout } from 'rxjs';
import { RideService } from '@app/core/services/ride-service';

@Component({
  selector: 'app-ride-info',
  templateUrl: './ride-info.html',
  styleUrl: './ride-info.css',
})
export class RideInfo implements OnInit, OnDestroy {
  private router = inject(Router);
  navigateToCancelRide() {
    this.router.navigate([`/cancel-ride/${this.rideId}`]);
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
  
  rideId = 1;
  private rideService = inject(RideService);
  private nowIntervalId: any;
  private now = signal(new Date());
  private driverService = inject(DriverService);
  private mapService = inject(MapService);
  private dialogService = inject(DialogService);
  
  Math = Math;
  
  // Inputs from parent
  rideOverview = input<RideOverviewModel | null>(null);
  isReported = input<boolean>(false);
  isReviewed = input<boolean>(false);

  rideCancelled = output();
  
  // Outputs
  reportClicked = output();
  onPanicClick() {
    this.dialogService.open(
      'Panic confirmation',
      'Are you sure you want to send a panic alert? This will notify the administrators and mark your vehicle as in danger.',
      true
    );
    // On real confirm, call backend and update map marker
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

  cancelRide(){
    this.rideCancelled.emit();
  }

  ngOnDestroy() {
    clearInterval(this.nowIntervalId);
  }
}