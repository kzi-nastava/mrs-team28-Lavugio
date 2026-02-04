import { Component, ViewChild, OnInit, AfterViewInit, signal } from '@angular/core';
import { MapComponent } from "@app/shared/components/map/map";
import { Button } from "@app/shared/components/button/button";
import { Router } from '@angular/router';
import { DriverService } from '@app/core/services/driver-service';
import { DriverMarkerLocation } from '@app/shared/models/driverMarkerLocation';
import { MarkerIcons } from '@app/shared/components/map/marker-icons';
import { FormBackgroundSheet } from "../form-background-sheet/form-background-sheet";
import { DestinationSelector } from '../find-trip/destination-selector/destination-selector';
import { DestinationsDisplay } from '../find-trip/destinations-display/destinations-display';
import { TripDestination } from '@app/shared/models/tripDestination';
import { GeocodeResult } from '../find-trip/geocoding-service/geocoding-service';
import { Coordinates } from '@app/shared/models/coordinates';
import { TripStatsDisplay } from "../find-trip/trip-stats-display/trip-stats-display";
import { GeocodingService } from '../find-trip/geocoding-service/geocoding-service';

@Component({
  selector: 'app-guest-home-page',
  imports: [MapComponent, Button, FormBackgroundSheet, DestinationSelector, DestinationsDisplay, TripStatsDisplay],
  templateUrl: './guest-home-page.html',
  styleUrl: './guest-home-page.css',
})
export class GuestHomePage implements AfterViewInit{
  @ViewChild('map') mapComponent!: MapComponent;
  @ViewChild('destinationSelector') destinationSelector!: DestinationSelector;
  private intervalId: any;
  destinations: TripDestination[] = [];
  isMapPickMode = false;
  
  // Trip stats
  distance = signal<string>('0km');
  estimatedTime = signal<string>('0min');

  constructor(
    private router: Router,
    private driverService: DriverService,
    private geocodingService: GeocodingService
  ) {}

  navigateToRegister() {
    this.router.navigate(['/register']);
  }

  ngAfterViewInit() {
      this.loadDriverMarkers();

      this.intervalId = setInterval(() => this.loadDriverMarkers(), 120_000);
  }

  sendToRegistrationPage() {
    this.router.navigate(['/register']);
  }

  scrollDown() {
    const screenHeight = window.innerHeight;
    window.scrollTo({ top: screenHeight, behavior: 'smooth' });
  }

  private loadDriverMarkers() {
    this.driverService.getDriverLocations().subscribe({
      next: (locations: DriverMarkerLocation[]) => {
        console.log('Got locations from backend:', locations);
        this.mapComponent.resetMarkers();
        locations.forEach(loc => {
          this.mapComponent.addMarker(
            { latitude: loc.location.latitude, longitude: loc.location.longitude },
            this.getMarkerIconByStatus(loc.status)
          );
        });
      },
      error: (err) => console.error('Error loading driver locations:', err)
    });
  }

  private getMarkerIconByStatus(status: string) {
    switch (status) {
      case 'AVAILABLE':
        return MarkerIcons.driverAvailable;
      case 'BUSY':
        return MarkerIcons.driverBusy;
      case 'RESERVED':
        return MarkerIcons.driverReserved;
      default:
        return MarkerIcons.default;
    }
  }

  ngOnDestroy() {
    clearInterval(this.intervalId);
  }

  onDestinationAdded(geocodeResult: GeocodeResult) {
    const newDestination: TripDestination = {
      id: geocodeResult.place_id?.toString() || crypto.randomUUID(),
      name: geocodeResult.display_name,
      street: geocodeResult.street || '',
      houseNumber: geocodeResult.streetNumber || '',
      city: geocodeResult.city || '',
      country: geocodeResult.country || '',
      coordinates: {
        latitude: Number(geocodeResult.lat),
        longitude: Number(geocodeResult.lon),
      },
    };

    this.destinations.push(newDestination);
    this.mapComponent.addMarker(newDestination.coordinates, MarkerIcons.checkpoint);
    this.updateRoute();
  }

  onDestinationRemoved(destinationId: string) {
    this.destinations = this.destinations.filter((d) => d.id !== destinationId);

    this.mapComponent.resetMarkers();
    this.mapComponent.removeRoute();

    // Re-add markers in correct order
    this.destinations.forEach((d, index) => {
      const icon =
        index === 0
          ? MarkerIcons.start
          : index === this.destinations.length - 1
          ? MarkerIcons.end
          : MarkerIcons.checkpoint;

      this.mapComponent.addMarker(d.coordinates, icon);
    });

    this.updateRoute();
  }

  private updateRoute() {
    if (this.destinations.length < 2) {
      this.distance.set('0km');
      this.estimatedTime.set('0min');
      return;
    }

    const coords = this.destinations.map((d) => d.coordinates);
    this.mapComponent.setRoute(coords);
    
    // Calculate route statistics
    this.geocodingService.getRouteInfo(coords).subscribe({
      next: (routeInfo) => {
        if (!routeInfo) {
          console.error('Failed to get route info');
          return;
        }
        
        // Convert distance from meters to km
        const distanceKm = (routeInfo.distanceMeters / 1000).toFixed(1);
        this.distance.set(`${distanceKm}km`);
        
        // Convert duration from seconds to minutes
        const durationMinutes = Math.round(routeInfo.durationSeconds / 60);
        this.estimatedTime.set(`${durationMinutes}min`);
      },
      error: (error) => {
        console.error('Failed to get route info:', error);
        this.distance.set('0km');
        this.estimatedTime.set('0min');
      },
    });
  }

  enableMapPickMode() {
    this.isMapPickMode = true;
  }

  onMapClicked(coords: Coordinates) {
    if (this.isMapPickMode) {
      this.onMapPicked(coords);
    }
  }

  onMapPicked(coords: Coordinates) {
    this.destinationSelector.setLocationFromMap(coords);
    this.isMapPickMode = false;
    this.mapComponent.clickedLocation.set(null);
  }

}
