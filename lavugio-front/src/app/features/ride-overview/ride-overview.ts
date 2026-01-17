import { AfterViewInit, Component, ViewChild, effect, Injector, inject, EffectRef, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { MapComponent } from '@app/shared/components/map/map';
import { RideInfo } from './ride-info/ride-info';
import { MarkerIcons } from '@app/shared/components/map/marker-icons';
import { Coordinates } from '@app/shared/models/coordinates';
import { Marker } from 'leaflet';
import { RideService } from '@app/core/services/ride-service';
import { DriverService } from '@app/core/services/driver-service';
import { ReportForm } from "./report-form/report-form";
import { ReviewForm } from '@app/shared/components/review-form/review-form';
import { RideOverviewModel } from '@app/shared/models/rideOverview';
import { catchError, EMPTY, timeout, Subscription } from 'rxjs';

@Component({
  selector: 'app-ride-overview',
  imports: [Navbar, MapComponent, RideInfo, ReportForm, ReviewForm],
  templateUrl: './ride-overview.html',
  styleUrl: './ride-overview.css',
})
export class RideOverview implements AfterViewInit {
  private injector = inject(Injector);
  private rideService = inject(RideService);
  private driverService = inject(DriverService);
  
  isInfoOpen = signal(false);
  isDesktop = signal(window.innerWidth >= 1024);
  showReport = signal(false);
  showReview = signal(false);
  isRated = signal(false);
  isReported = signal(false);
  
  rideOverview = signal<RideOverviewModel | null>(null);
  rideId: number = 1;
  
  private intervalId: any;
  private subscription: Subscription | null = null;
  private router = inject(Router);
  navigateToCancelRide() {
    this.router.navigate([`/cancel-ride/${this.rideId}`]);
  }

  @ViewChild('rideInfo') rideInfo!: RideInfo;
  @ViewChild('map') mapComponent!: MapComponent;

  constructor() {
    window.addEventListener('resize', () => {
      this.isDesktop.set(window.innerWidth >= 1024);
    });
  }

  ngOnInit(): void {
    this.fetchRideOverview(this.rideId);
    this.createTopicSubscription(this.rideId);
  }

  ngAfterViewInit(): void {
    effect(() => {
      const overview = this.rideOverview();
      if (overview && overview.checkpoints && overview.checkpoints.length > 0) {
        const points = overview.checkpoints;
        console.log("Points:", points);
        this.mapComponent?.setRoute(points);
        this.mapComponent?.addMarker(points[0], MarkerIcons.start);
        this.mapComponent?.addMarker(points[points.length - 1], MarkerIcons.end);
      }
    }, { injector: this.injector });

    effect(() => {
      const overview = this.rideOverview();
      if (overview?.driverId) {
        this.executeInterval(overview.driverId);
      }
    }, { injector: this.injector });
  }

  fetchRideOverview(rideId: number): void {
    this.subscription = this.rideService.getRideOverview(rideId).pipe(
      timeout(5000),
      catchError(err => {
        console.error('Error fetching ride overview:', err);
        this.rideOverview.set(null);
        return EMPTY;
      })
    ).subscribe(overview => {
      console.log('Ride Overview fetched:', overview);
      this.rideOverview.set(overview);
      this.isReported.set(overview.reported ? true : false);
      this.isRated.set(overview.reviewed ? true : false);
      console.log(overview.reported)
      console.log(overview.reviewed)
    });
  }

  toggleInfo() {
    this.isInfoOpen.update(v => !v);
  }

  closeInfo() {
    this.isInfoOpen.set(false);
  }

  openInfo() {
    this.isInfoOpen.set(true);
  }

  executeInterval(driverId: number): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }

    this.driverService.getDriverLocation(driverId).subscribe((location: { location: Coordinates }) => {
      let mark = this.addDriverLocationMarker(location.location);
      
      this.intervalId = setInterval(() => {
        this.driverService.getDriverLocation(driverId).subscribe((newLocation: { location: Coordinates }) => {
          if (mark) {
            this.mapComponent?.removeMarker(mark);
          }
          mark = this.addDriverLocationMarker(newLocation.location);
        });
      }, 60000);
    });
  }

  addDriverLocationMarker(driverLoc: Coordinates | null): Marker | undefined {
    let mark: Marker | undefined;
    if (driverLoc) {
      mark = this.mapComponent?.addMarker(driverLoc, MarkerIcons.driverAvailable);
      console.log('Added driver location marker at:', driverLoc);
    } else {
      console.warn('Driver location is not available to add marker.');
    }
    return mark;
  }

  createTopicSubscription(rideId: number): void {
    this.rideService.listenToRideUpdates(rideId).subscribe(update => {
      console.log('Received ride update via WebSocket:', update);
      const currentOverview = this.rideOverview();
      if (currentOverview) {
        const updatedOverview = this.applyRideOverviewUpdate(currentOverview, update);
        this.rideOverview.set(updatedOverview);
      }
    });
  }

  applyRideOverviewUpdate(current: RideOverviewModel, update: any): RideOverviewModel {
    const updatedCheckpoints = update.destinationCoordinates !== undefined
      ? [...current.checkpoints.slice(0, -1), update.destinationCoordinates]
      : current.checkpoints;

    return {
      ...current,
      endAddress: update.endAddress !== undefined ? update.endAddress : current.endAddress,
      checkpoints: updatedCheckpoints,
      status: update.status !== undefined ? update.status : current.status,
      price: update.price !== undefined ? update.price : current.price,
      departureTime: update.departureTime !== undefined 
        ? (update.departureTime ? new Date(update.departureTime) : null)
        : current.departureTime,
      arrivalTime: update.arrivalTime !== undefined
        ? (update.arrivalTime ? new Date(update.arrivalTime) : null)
        : current.arrivalTime,
    };
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    clearInterval(this.intervalId);
    this.rideService.closeConnection();
    window.removeEventListener('resize', () => {
      this.isDesktop.set(window.innerWidth >= 1024);
    });
  }
}