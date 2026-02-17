import { AfterViewInit, Component, computed, effect, inject, Injector, OnDestroy, signal, ViewChild } from '@angular/core';
import { Navbar } from "@app/shared/components/navbar/navbar";
import { Ride } from './ride/ride';
import { MapComponent } from "@app/shared/components/map/map";
import { RideMonitoringModel } from '@app/shared/models/ride/rideMonitoring';
import { Coordinates } from '@app/shared/models/coordinates';
import { RideService } from '@app/core/services/ride-service';
import { catchError, EMPTY, Subscription, timeout } from 'rxjs';
import { WebSocketService } from '@app/core/services/web-socket-service';
import { IMessage, StompSubscription } from '@stomp/stompjs';
import * as L from 'leaflet';
import { MarkerIcons } from '@app/shared/components/map/marker-icons';
import { DriverService } from '@app/core/services/user/driver-service';

@Component({
  selector: 'app-ride-monitoring',
  imports: [Navbar, Ride, MapComponent],
  templateUrl: './ride-monitoring.html',
  styleUrl: './ride-monitoring.css',
})
export class RideMonitoring implements AfterViewInit, OnDestroy {
  rides = signal<RideMonitoringModel[] | null>([]);
  coordinates = signal<Coordinates[] | null>(null);
  @ViewChild("map") map! : MapComponent;
  private injector = inject(Injector);
  private rideService = inject(RideService);
  private subscription : Subscription | null = null;
  private webSocketService = inject(WebSocketService);
  private driverMarker: L.Marker | null = null;
  private driverService = inject(DriverService);
  private rideStartSub: StompSubscription | null = null;
  private rideFinishSub: StompSubscription | null = null;
  private locationSub: StompSubscription | null = null;
  private selectedDriverId: number | null = null;
  filterText = signal<string>('');


  constructor() {}

  ngAfterViewInit(): void {
    this.loadRides();

    this.webSocketService.connect(() => {
      this.subscribeToRideStart();
      this.subscribeToRideFinish();
    });

    effect(() => {
      let coords = this.coordinates();
      if (coords && this.map) {
        this.map.setRoute(coords);
      }
    }, { injector: this.injector });
  }

  loadRides(){
    this.subscription = this.rideService.getActiveRides().pipe(
          timeout(5000),
          catchError(err => {
            console.error('Error fetching ride overview:', err);
            this.rides.set(null);
            return EMPTY;
          })
        ).subscribe(rides => {
          console.log('Ride Overview fetched:', rides);
          this.rides.set(rides);
        });
  }

  updateCoordinates(coordinates : Coordinates[] | null){
    this.coordinates.set(coordinates);
  }

  removeRideFromList(rideId: number){
    const currentRides = this.rides();
    if (!currentRides) return;

    const updatedRides = currentRides.filter(ride => ride.rideId !== rideId);
    
    this.rides.set(updatedRides);
  }

  private subscribeToRideStart() {
    this.rideStartSub = this.webSocketService.subscribe(
      '/socket-publisher/ride/start',
      (message: IMessage) => {
        const newRide: RideMonitoringModel = JSON.parse(message.body);

        const current = this.rides() || [];
        this.rides.set([...current, newRide]);
      }
    );
  }

  private subscribeToRideFinish() {
    this.rideFinishSub = this.webSocketService.subscribe(
      '/socket-publisher/ride/finish',
      (message: IMessage) => {
        const rideId: number = JSON.parse(message.body);

        const wasSelected =
          this.rides()?.some(
            r => r.rideId === rideId && r.driverId === this.selectedDriverId
          );

        if (wasSelected) {
          this.unsubscribeFromLocation();

          if (this.map) {
            this.map.removeRoute();
            this.map.resetMarkers();
          }

          this.coordinates.set(null);
          this.selectedDriverId = null;
        }

        this.removeRideFromList(rideId);
      }
    );
  }

  subscribeToDriverLocation(driverId: number) {

    if (this.selectedDriverId === driverId) return;

    this.unsubscribeFromLocation();
    this.selectedDriverId = driverId;

    this.locationSub = this.webSocketService.subscribe(
      `/socket-publisher/location/${driverId}`,
      (message) => {

        const coords: Coordinates = JSON.parse(message.body);

        if (!this.map) return;

        if (this.driverMarker) {
          this.driverMarker.setLatLng([coords.latitude, coords.longitude]);
        } 
        else {
          this.driverMarker = this.map.addMarker(
            coords,
            MarkerIcons.driverAvailable
          );
        }
      }
    );
  }

  private unsubscribeFromLocation() {
    if (this.locationSub) {
      this.locationSub.unsubscribe();
      this.locationSub = null;
    }

    if (this.driverMarker && this.map) {
      this.map.removeMarker(this.driverMarker);
      this.driverMarker = null;
    }

    this.selectedDriverId = null;
  }

  ngOnDestroy(): void {
    this.rideStartSub?.unsubscribe();
    this.rideFinishSub?.unsubscribe();
    this.unsubscribeFromLocation();
    this.subscription?.unsubscribe();
  }

  onRideSelected(ride: RideMonitoringModel) {

    if (this.selectedDriverId === ride.driverId) return;

    if (ride.checkpoints && ride.checkpoints.length > 1) {
      this.coordinates.set(ride.checkpoints);
    }

    this.unsubscribeFromLocation();

    this.driverService.getDriverLocation(ride.driverId).subscribe({
      next: (driverLocation) => {

        const coords: Coordinates = {
          latitude: driverLocation.location.latitude,
          longitude: driverLocation.location.longitude
        };

        if (this.driverMarker && this.map) {
          this.map.removeMarker(this.driverMarker);
        }

        this.driverMarker = this.map.addMarker(
          coords,
          MarkerIcons.driverAvailable
        );

        this.subscribeToDriverLocation(ride.driverId);
      },
      error: (err) => {
        console.error("Error loading driver location:", err);
      }
    });
  }

  filteredRides = computed(() => {
    const allRides = this.rides() || [];
    const filter = this.filterText().toLowerCase();

    if (!filter) return allRides;

    return allRides.filter(r => r.driverName?.toLowerCase().includes(filter));
  });

  loadMockRides() {
    const mockRides: RideMonitoringModel[] = [
      {
        rideId: 1,
        driverId: 101,
        driverName: "Vukasin",
        startTime: new Date("2026-02-09T10:00:00Z"),
        startAddress: "Bulevar Oslobodjenja 10",
        endAddress: "Knez Mihailova 5",
        checkpoints: [
          { latitude: 44.8176, longitude: 20.4569 },
          { latitude: 44.8180, longitude: 20.4575 },
        ]
      },
      {
        rideId: 2,
        driverId: 102,
        driverName: "Jovana",
        startTime: new Date("2026-02-09T10:30:00Z"),
        startAddress: "Bulevar Kralja Aleksandra 50",
        endAddress: "Trg Republike 2",
        checkpoints: [
          { latitude: 44.8170, longitude: 20.4570 },
          { latitude: 44.8190, longitude: 20.4590 },
        ]
      },
      {
        rideId: 3,
        driverId: 103,
        driverName: "Ana",
        startTime: new Date("2026-02-09T11:00:00Z"),
        startAddress: "Nemanjina 25",
        endAddress: "Terazije 8",
        checkpoints: [
          { latitude: 44.8160, longitude: 20.4550 },
          { latitude: 44.8185, longitude: 20.4580 },
        ]
      },
      {
        rideId: 4,
        driverId: 101,
        driverName: "Vukasin",
        startTime: new Date("2026-02-09T10:00:00Z"),
        startAddress: "Bulevar Oslobodjenja 10",
        endAddress: "Knez Mihailova 5",
        checkpoints: [
          { latitude: 44.8176, longitude: 20.4569 },
          { latitude: 44.8180, longitude: 20.4575 },
        ]
      },
      {
        rideId: 5,
        driverId: 101,
        driverName: "Vukasin",
        startTime: new Date("2026-02-09T10:00:00Z"),
        startAddress: "Bulevar Oslobodjenja 10",
        endAddress: "Knez Mihailova 5",
        checkpoints: [
          { latitude: 44.8176, longitude: 20.4569 },
          { latitude: 44.8180, longitude: 20.4575 },
        ]
      },
      {
        rideId: 6,
        driverId: 101,
        driverName: "Vukasin",
        startTime: new Date("2026-02-09T10:00:00Z"),
        startAddress: "Bulevar Oslobodjenja 10",
        endAddress: "Knez Mihailova 5",
        checkpoints: [
          { latitude: 44.8176, longitude: 20.4569 },
          { latitude: 44.8180, longitude: 20.4575 },
        ]
      },
    ];

    this.rides.set(mockRides);
  }
}
