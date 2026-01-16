import { Component, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { MapComponent } from "@app/shared/components/map/map";
import { Button } from "@app/shared/components/button/button";
import { Router } from '@angular/router';
import { DriverService } from '@app/core/services/driver-service';
import { DriverMarkerLocation } from '@app/shared/models/driverMarkerLocation';
import { MarkerIcons } from '@app/shared/components/map/marker-icons';

@Component({
  selector: 'app-guest-home-page',
  imports: [MapComponent, Button],
  templateUrl: './guest-home-page.html',
  styleUrl: './guest-home-page.css',
})
export class GuestHomePage implements AfterViewInit{
  @ViewChild('map') mapComponent!: MapComponent;
  private intervalId: any;

  constructor(
    private router: Router,
    private driverService: DriverService
  ) {}

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

}
