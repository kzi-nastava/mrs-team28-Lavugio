import { AfterViewInit, Component, inject, OnDestroy, signal, ViewChild } from '@angular/core';
import { BaseInfoPage } from '@app/features/base-info-page/base-info-page';
import { DriverInfo } from './driver-info/driver-info';
import { TripInfo } from './trip-info/trip-info';
import { ReportsSection } from './reports-section/reports-section';
import { ReviewSection } from './review-section/review-section';
import { MapComponent } from '@app/shared/components/map/map';
import { RideHistoryUserDetailedModel } from '@app/shared/models/ride/rideHistoryUserDetailed';
import { catchError, EMPTY, Subscription, timeout } from 'rxjs';
import { UserService } from '@app/core/services/user/user-service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { DialogService } from '@app/core/services/dialog-service';
import { RideService } from '@app/core/services/ride-service';
import { RideRequestDTO } from '@app/shared/models/ride/rideRequestDTO';
import { VehicleType } from '@app/shared/models/vehicleType';
import { ReviewForm } from "@app/shared/components/review-form/review-form";

@Component({
  selector: 'app-trip-history-user-detailed',
  imports: [BaseInfoPage, DriverInfo, TripInfo, ReportsSection, ReviewSection, MapComponent, CommonModule, ReviewForm],
  templateUrl: './trip-history-user-detailed.html',
  styleUrl: './trip-history-user-detailed.css',
})
export class TripHistoryUserDetailed implements AfterViewInit, OnDestroy {
  rideIdStr: string | null = '';
  rideId!: number | null;
  ride = signal<RideHistoryUserDetailedModel | null>(null);
  sub!: Subscription;
  userService = inject(UserService);
  activatedRoute = inject(ActivatedRoute);
  router = inject(Router);
  dialogService = inject(DialogService);
  rideService = inject(RideService);
  @ViewChild('map') map!: MapComponent;

  ngAfterViewInit(): void {
    this.rideIdStr = this.activatedRoute.snapshot.paramMap.get('rideId');
    if (this.rideIdStr) {
      this.rideId = parseInt(this.rideIdStr);
    } else {
      console.error("Couldn't parse ride id");
      return;
    }
    this.fetchRide();
  }

  fetchRide(): void {
    const rideId = this.rideId;
    if (rideId == undefined) {
      console.error('RideId is undefined');
      return;
    }
    this.sub = this.userService
      .getUserRideHistoryDetailed(rideId)
      .pipe(
        timeout(5000),
        catchError((err) => {
          console.error('Error fetching ride overview:', err);
          this.ride.set(null);
          return EMPTY;
        })
      )
      .subscribe((ride) => {
        this.ride.set(ride);
        this.map.setRoute(ride.checkpoints);
        console.log('Ride fetched: ', ride);
      });
  }

  reorderRide() {
    const ride = this.ride();
    if (!ride) return;

    // Check if user can order a ride first
    this.userService.canUserOrderRide().subscribe({
      next: (response) => {
        if (response.block.blocked) {
          this.dialogService.openBlocked(response.block.reason);
        } else if (response.isInRide) {
          this.dialogService.open(
            'Cannot Order Ride',
            'You are already in an active ride and cannot order a new one.',
            true
          );
        } else {
          this.openScheduleDialog();
        }
      },
      error: (error) => {
        console.error('Error checking ride eligibility:', error);
        this.dialogService.open('Error', 'Unable to check ride eligibility. Please try again.', true);
      }
    });
  }

  openScheduleDialog() {
    const ride = this.ride();
    if (!ride) return;

    this.dialogService.openScheduleRide().subscribe({
      next: (result) => {
        console.log('Schedule data:', result);

        // Build the ride request from history data
        const rideRequest: RideRequestDTO = {
          destinations: ride.destinations.map((d) => ({
            location: {
              orderIndex: d.orderIndex,
              latitude: d.latitude,
              longitude: d.longitude,
            },
            address: d.address,
            streetName: d.streetName,
            city: d.city,
            country: d.country,
            streetNumber: parseInt(d.streetNumber) || 0,
            zipCode: d.zipCode,
          })),
          passengerEmails: [], // Current user only, backend handles this
          vehicleType: 'STANDARD' as VehicleType, // Default, backend will assign available driver
          babyFriendly: false,
          petFriendly: false,
          scheduledTime: result.scheduledTime ? new Date(result.scheduledTime).toISOString().replace('Z', '') : '',
          scheduled: result.isScheduled,
          estimatedDurationSeconds: 0, // Will be calculated by backend
          distance: 0, // Will be calculated by backend
          price: ride.price, // Use previous price as estimate
        };

        console.log('Creating ride with:', rideRequest);
        this.findRide(rideRequest);
      },
      complete: () => {
        console.log('Modal closed');
      },
    });
  }

  findRide(rideRequest: RideRequestDTO) {
    this.rideService.findRide(rideRequest).subscribe({
      next: (response) => {
        console.log('Ride found:', response);
        this.dialogService.open(
          'Ride Ordered',
          'Your ride has been successfully ordered.',
          false
        );
        setTimeout(() => {
          this.router.navigate(['/']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error finding ride:', error);
        
        // Extract error message from backend response
        let errorMessage = 'Unable to order a ride at this time. Please try again later.';
        
        if (error.error) {
          // Backend returns error message as plain text or in error object
          if (typeof error.error === 'string') {
            errorMessage = error.error;
          } else if (error.error.message) {
            errorMessage = error.error.message;
          }
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        this.dialogService.open('Error', errorMessage, true);
      },
    });
  }

  showReview = signal(false);

  openReview() {
    this.showReview.set(true);
  }

  onReviewSuccess(updatedReview: { driverRating: number, carRating: number, reviewComment: string }) {
    const currentRide = this.ride();
    if (!currentRide) return;

    this.ride.set({
      ...currentRide,
      hasReview: true,
      driverRating: updatedReview.driverRating,
      carRating: updatedReview.carRating,
      reviewComment: updatedReview.reviewComment,
    });

    this.showReview.set(false);
  }


  ngOnDestroy(): void {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }
}
