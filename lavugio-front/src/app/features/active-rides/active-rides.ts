import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RideService } from '@app/core/services/ride-service';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { DialogService } from '@app/core/services/dialog-service';
import { LiveSupportButtonComponent } from "@app/shared/components/live-support-button/live-support-button";

interface Ride {
  id: number;
  rideStatus: string;
  startLocation?: string;
  endLocation?: string;
  startDateTime?: string;
  price?: number;
}

@Component({
  selector: 'app-active-rides',
  imports: [CommonModule, Navbar, LiveSupportButtonComponent],
  templateUrl: './active-rides.html',
  styleUrl: './active-rides.css'
})
export class ActiveRides implements OnInit {
  activeRides: Ride[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(
    private rideService: RideService,
    public router: Router,
    private cdr: ChangeDetectorRef,
    private dialogService: DialogService
  ) {}

  ngOnInit() {
    this.loadActiveRides();
  }

  loadActiveRides() {
    this.isLoading = true;
    this.error = null;
    
    this.rideService.getUserActiveRides().subscribe({
      next: (rides: Ride[]) => {
        console.log('Received rides:', rides);
        this.activeRides = rides;
        this.isLoading = false;
        this.cdr.detectChanges(); // Force change detection
      },
      error: (err) => {
        console.error('Error loading active rides:', err);
        console.error('Error status:', err.status);
        console.error('Error message:', err.message);
        console.error('Error body:', err.error);
        this.error = 'Failed to load active rides. Please try again later.';
        this.isLoading = false;
        this.cdr.detectChanges(); // Force change detection
      }
    });
  }

  viewRideDetails(rideId: number) {
    this.router.navigate([`/${rideId}/ride-overview`]);
  }

  formatDate(date: any): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleString();
  }

  getRideStatusClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'SCHEDULED':
        return 'bg-blue-100 text-blue-800';
      case 'STOPPED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  canCancelRide(ride: Ride): boolean {
    if (!ride.startDateTime) return false;
    
    const now = new Date();
    const startTime = new Date(ride.startDateTime);
    const minutesUntilStart = (startTime.getTime() - now.getTime()) / (1000 * 60);
    
    return minutesUntilStart >= 10;
  }

  cancelRide(rideId: number) {
    this.dialogService.openConfirm(
      'Cancel Ride',
      'Are you sure you want to cancel this ride? This action cannot be undone.'
    ).subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      
      this.executeCancelRide(rideId);
    });
  }

  private executeCancelRide(rideId: number): void {
    this.rideService.cancelRideByPassenger(rideId).subscribe({
      next: () => {
        console.log('✅ Ride canceled successfully:', rideId);
        alert('✅ Ride canceled successfully.');
        this.loadActiveRides(); // Reload the list
      },
      error: (err) => {
        console.error('❌ Failed to cancel ride:', err);
        const errorMessage = err.error?.error || err.message || 'Unknown error';
        alert('❌ Failed to cancel ride.\n\nError: ' + errorMessage);
      }
    });
  }
}
