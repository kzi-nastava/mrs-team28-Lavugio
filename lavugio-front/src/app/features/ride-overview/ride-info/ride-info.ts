import { Component, computed, signal, inject, OnInit, OnDestroy, effect, output } from '@angular/core';
import { DriverService } from '@app/core/services/driver-service';
import { MapService } from '@app/core/services/map-service';
import { RideService } from '@app/core/services/ride-service';
import { Coordinates } from '@app/shared/models/coordinates';
import { RideOverviewModel } from '@app/shared/models/rideOverview';
import { RideOverviewUpdate } from '@app/shared/models/rideOverviewUpdate';
import { catchError, EMPTY, timeout, Subscription, Observable } from 'rxjs';

@Component({
  selector: 'app-ride-info',
  templateUrl: './ride-info.html',
  styleUrl: './ride-info.css',
})
export class RideInfo implements OnInit, OnDestroy {
  rideId = 1;
  private rideService = inject(RideService);
  private nowIntervalId: any;
  private now = signal(new Date());
  private driverService = inject(DriverService);
  private mapService = inject(MapService);
  driverLocation = signal<Coordinates | null>(null);
  
  reportClicked = output();
  rideOverview = signal<RideOverviewModel | null>(null);
  departureTime = computed(() => this.rideOverview()?.departureTime ? this.formatDateTime(new Date(this.rideOverview()!.departureTime!)) : null);
  rideStatus = signal<string>('Loading...');
  checkpoints = signal<Coordinates[]>([]);
  duration = signal<number>(0);
  Math = Math

  timeElapsed = computed(() => {
    if (this.rideOverview()?.arrivalTime != null) {
      return Math.ceil((new Date(this.rideOverview()!.arrivalTime!).getTime() - new Date(this.rideOverview()!.departureTime!).getTime()) / 60000);
    }

    const departureTime = this.rideOverview()?.departureTime;
    if (departureTime) {
      const diffMs = this.now().getTime() - new Date(departureTime).getTime();
      return Math.ceil(diffMs / 60000);
    }
    return 0;
  });

  private subscription: Subscription | null = null;

  constructor() {
    effect(() => {
      const driver = this.driverLocation();
      const cps = this.checkpoints();
      if (driver && cps && cps.length > 0) {
        this.calculateDuration();
      }
    });
  } 

  ngOnInit() {
    this.fetchRideOverview(1);
    this.fetchDriverLocation(1);
    this.createOneMinuteInterval();
    this.driverLocation.set(null);
  }

  fetchRideOverview(rideId: number) {
    this.subscription = this.rideService.getRideOverview(1).pipe(
      timeout(5000),
      catchError(err => {
        console.error('Error fetching ride overview:', err);
        this.rideStatus.set('denied');
        this.rideOverview.set(null);
        return EMPTY;
      })
    ).subscribe(overview => {
      console.log('Ride Overview fetched:', overview);
      this.rideOverview.set(overview);
      this.rideStatus.set(overview.status);
      this.checkpoints.set(overview.checkpoints);

    });
  }

  createOneMinuteInterval() {
    this.nowIntervalId = setInterval(() => {
      this.now.set(new Date());
      this.calculateDuration();
    }, 60000);
  }

  calculateDuration(): void {
    const checkpoints = this.checkpoints();
    const driver = this.driverLocation();

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


  fetchDriverLocation(driverId: number) {
      this.driverService.getDriverLocation(driverId).pipe(
        timeout(5000),
        catchError(err => {
          console.error('Error fetching driver location:', err);
          this.driverLocation.set(null);
          return EMPTY;
        })
      ).subscribe(location => {
        console.log('Driver location fetched:', location);
        this.driverLocation.set(location.location);
      });
  }


  formatDateTime(date: Date | null | undefined): string {
    if (!date) return "Loading...";

    const pad = (n: number) => n.toString().padStart(2, '0');

    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
    const day = pad(date.getDate());
    const month = pad(date.getMonth() + 1); 
    const year = date.getFullYear();

    return `${hours}:${minutes} ${day}.${month}.${year}`;
  }

  applyRideOverviewUpdate(
    current: RideOverviewModel,
    update: RideOverviewUpdate
  ): RideOverviewModel {
    const updatedCheckpoints = update.destinationCoordinates !== undefined
      ? [
          ...current.checkpoints.slice(0, -1),
          update.destinationCoordinates
        ]
      : current.checkpoints;

    return {
      ...current,

      endAddress:
        update.endAddress !== undefined
          ? update.endAddress
          : current.endAddress,

      checkpoints: updatedCheckpoints,

      status:
        update.status !== undefined
          ? update.status
          : current.status,

      price:
        update.price !== undefined
          ? update.price
          : current.price,

      departureTime:
        update.departureTime !== undefined
          ? update.departureTime
            ? new Date(update.departureTime)
            : null
          : current.departureTime,

      arrivalTime:
        update.arrivalTime !== undefined
          ? update.arrivalTime
            ? new Date(update.arrivalTime)
            : null
          : current.arrivalTime,
    };
  }

  updateRideOverview(update: RideOverviewUpdate) {
    const currentOverview = this.rideOverview();
    if (currentOverview) {
      const updatedOverview = this.applyRideOverviewUpdate(currentOverview, update);
      this.rideOverview.set(updatedOverview);
      this.rideStatus.set(updatedOverview.status);
      
      if (update.destinationCoordinates !== undefined) {
        this.checkpoints.set(updatedOverview.checkpoints);
      }
    }
  }

  ngOnDestroy() {
    this.subscription?.unsubscribe();
    clearInterval(this.nowIntervalId);
  }

}
