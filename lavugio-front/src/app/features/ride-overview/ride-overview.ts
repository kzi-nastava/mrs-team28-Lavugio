import { AfterViewInit, Component, ViewChild, effect, Injector, inject, signal } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
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
import { RideOverviewModel } from '@app/shared/models/ride/rideOverview';
import { catchError, EMPTY, timeout, Subscription } from 'rxjs';
import { LocationService } from '@app/core/services/location-service';
import { computed } from '@angular/core';


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
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  isInfoOpen = signal(false);
  isDesktop = signal(window.innerWidth >= 1024);
  showReport = signal(false);
  showReview = signal(false);
  isRated = signal(false);
  isReported = signal(false);
  canRateRide = computed(() => {
    const overview = this.rideOverview();
    if (!overview) return false;

    if (overview.status !== 'FINISHED') return false;
    if (!overview.arrivalTime) return false;
    if (this.isRated()) return false;

    const arrival = new Date(overview.arrivalTime).getTime();
    const now = Date.now();

    const diffInDays = (now - arrival) / (1000 * 60 * 60 * 24);

    return diffInDays <= 3;
  });

  rideOverview = signal<RideOverviewModel | null>(null);
  rideId!: number;

  private intervalId: any;
  private subscription: Subscription | null = null;

  private locationService = inject(LocationService);
  private clientMarker?: Marker;
  private clientLocationInterval: any;

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
    const rideIdParam = this.route.snapshot.paramMap.get('rideId');
    if (rideIdParam) {
      this.rideId = +rideIdParam;
    } else {
      console.error("RideId missing from url");
      return;
    }

    this.fetchRideOverview(this.rideId);
    this.createTopicSubscription(this.rideId);
  }

  ngAfterViewInit(): void {
    effect(() => {
      const overview = this.rideOverview();
      if (overview && overview.checkpoints && overview.checkpoints.length > 0) {
        const points = overview.checkpoints;
        this.mapComponent?.setRoute(points);
        this.mapComponent?.addMarker(points[0], MarkerIcons.start);
        this.mapComponent?.addMarker(points[points.length - 1], MarkerIcons.end);
      }
    }, { injector: this.injector });

    effect(() => {
      const overview = this.rideOverview();

      if (!this.mapComponent || !overview) return;

      if (overview.status === 'ACTIVE') {
        this.startTrackingClientLocation();
      } else {
        this.stopTrackingClientLocation();
      }
    }, { injector: this.injector });
  }

  fetchRideOverview(rideId: number): void {
    this.subscription = this.rideService.getRideOverview(rideId).pipe(
      timeout(5000),
      catchError(err => {
        console.error('Error fetching ride overview:', err);
        this.rideOverview.set(null);
        
        // If ride not found or access forbidden, redirect to active rides
        if (err.status === 404 || err.status === 403) {
          alert('This ride is not available. Redirecting to active rides...');
          this.router.navigate(['/active-rides']);
        }
        
        return EMPTY;
      })
    ).subscribe(overview => {
      this.rideOverview.set(overview);
      this.isReported.set(overview.reported ? true : false);
      this.isRated.set(overview.reviewed ? true : false);
      
      // Warn if ride is not ACTIVE
      if (overview.status && overview.status !== 'ACTIVE') {
        console.warn(`Viewing ride with status: ${overview.status}`);
      }
    });
  }

  toggleInfo() { this.isInfoOpen.update(v => !v); }
  closeInfo() { this.isInfoOpen.set(false); }
  openInfo() { this.isInfoOpen.set(true); }

  executeInterval(driverId: number): void {
    if (this.intervalId) clearInterval(this.intervalId);

    this.driverService.getDriverLocation(driverId).subscribe((location: { location: Coordinates }) => {
      let mark = this.addDriverLocationMarker(location.location);

      this.intervalId = setInterval(() => {
        this.driverService.getDriverLocation(driverId).subscribe((newLocation: { location: Coordinates }) => {
          if (mark) this.mapComponent?.removeMarker(mark);
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

  cancelRide() {
    const current = this.rideOverview();
    if (current != null) {
      this.rideOverview.set({
        ...current,
        status: "CANCELLED"
      });
    }
  }


  startTrackingClientLocation(): void {
    this.getLocation();

    this.clientLocationInterval = setInterval(() => {
      this.getLocation()}, 10000);
  }

  getLocation(){
    this.locationService.getLocation()
        .then(position => {
          const coords: Coordinates = {
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          };

          if (this.clientMarker) {
            this.mapComponent?.removeMarker(this.clientMarker);
          }

          if (this.rideInfo){
            this.rideInfo.userLocation.set(coords);
            this.rideInfo.calculateDuration();
          }

          this.clientMarker = this.mapComponent?.addMarker(
            coords,
            MarkerIcons.driverAvailable
          );
        })
        .catch(err => {
          console.error('Location error:', err);
          // Show user-friendly error message with instructions
          if (err.code === 1) { // PERMISSION_DENIED
            console.warn(
              '⚠️ Location permission denied. ' +
              'Click the lock icon in the address bar and allow location access.'
            );
          }
        })
  }

  stopTrackingClientLocation(): void {
    if (this.clientLocationInterval) {
      clearInterval(this.clientLocationInterval);
      this.clientLocationInterval = null;
    }

    if (this.clientMarker) {
      this.mapComponent?.removeMarker(this.clientMarker);
      this.clientMarker = undefined;
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    clearInterval(this.intervalId);
    clearInterval(this.clientLocationInterval);
    this.rideService.closeConnection();
    window.removeEventListener('resize', () => {
      this.isDesktop.set(window.innerWidth >= 1024);
    });
  }
}
