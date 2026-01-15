import { AfterViewInit, Component, ViewChild, effect, Injector, inject, EffectRef, signal } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { MapComponent } from '@app/shared/components/map/map';
import { RideInfo } from './ride-info/ride-info';
import { MarkerIcons } from '@app/shared/components/map/marker-icons';
import { Coordinates } from '@app/shared/models/coordinates';
import { Marker } from 'leaflet';
import { RideService } from '@app/core/services/ride-service';
import { ReportForm } from "./report-form/report-form";

@Component({
  selector: 'app-ride-overview',
  imports: [Navbar, MapComponent, RideInfo, ReportForm],
  templateUrl: './ride-overview.html',
  styleUrl: './ride-overview.css',
})
export class RideOverview implements AfterViewInit {
  private injector = inject(Injector);
  isInfoOpen = signal(false);
  isDesktop = signal(window.innerWidth >= 1024);
  private intervalId: any;
  private rideService = inject(RideService);
  rideId: number = 1; 
  showReport = signal(false);

  @ViewChild('rideInfo') rideInfo!: RideInfo;
  @ViewChild('map') mapComponent!: MapComponent;

  constructor() {
    window.addEventListener('resize', () => {
      this.isDesktop.set(window.innerWidth >= 1024);
    });
  }

  ngAfterViewInit(): void {
    this.createTopicSubscription(this.rideId);

    effect(() => {
      const points = this.rideInfo?.checkpoints();
      if (points && points.length > 0) {
        console.log("Points:", points);
        this.mapComponent?.setRoute(this.rideInfo.checkpoints());
        this.executeInterval();
        this.mapComponent?.addMarker(points[0], MarkerIcons.start);
        this.mapComponent?.addMarker(points[points.length - 1], MarkerIcons.end);
      }
    }, { injector: this.injector });
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

  executeInterval(): void {
    let mark = this.addDriverLocationMarker(this.rideInfo.driverLocation());
    this.intervalId = 
      setInterval(() => {
        if (mark) {
          this.mapComponent?.removeMarker(mark);
        }
        mark = this.addDriverLocationMarker(this.rideInfo.driverLocation());
      }, 60000);
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
    this.rideService.listenToRideUpdates(rideId).subscribe(update  => {
      console.log('Received ride update via WebSocket:', update);
      this.rideInfo.updateRideOverview(update);
    });
  }

  ngOnDestroy(): void {
    clearInterval(this.intervalId);
    this.rideService.closeConnection();
    window.removeEventListener('resize', () => {
      this.isDesktop.set(window.innerWidth >= 1024);
    });
  }
}