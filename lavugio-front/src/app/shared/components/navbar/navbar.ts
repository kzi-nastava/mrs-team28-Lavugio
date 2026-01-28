import { Component, OnInit, signal, NgZone, ChangeDetectorRef } from '@angular/core';
import { Links } from './links/links';
import { Logo } from './links/logo/logo';
import { Link } from './links/link/link';
import { AuthService, LoginResponse } from '@app/core/services/auth-service';
import { NotificationService } from '@app/core/services/notification-service';
import { DriverStatusService } from '@app/core/services/driver-status.service';
import { RideService } from '@app/core/services/ride-service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '@app/core/services/user/user-service';
import { timeout } from 'rxjs';
import { DriverService } from '@app/core/services/user/driver-service';
import { Coordinates } from '@app/shared/models/coordinates';

@Component({
  selector: 'app-navbar',
  imports: [Links, Logo, Link, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit {
  isMenuOpen: boolean = false;
  isAuthenticated: boolean = false;
  currentUser: LoginResponse | null = null;
  isAdmin: boolean = false;
  isDriver: boolean = false;
  isRegularUser: boolean = false;
  driverActive: boolean = false;
  statusLoading: boolean = false;
  hasLatestRide = signal(false);
  latestRideId = signal(0);
  latestRideStatus = signal("");

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private driverStatusService: DriverStatusService,
    private driverService: DriverService,
    private rideService: RideService,
    private ngZone: NgZone,
    private cdr: ChangeDetectorRef,
    private regularUserService: UserService
  ) {
    if (authService.isRegularUser()){
      this.getLatestRide();
    }
  }

  ngOnInit() {
    // Check current authentication state
    this.isAuthenticated = this.authService.isAuthenticated();
    this.currentUser = this.authService.getStoredUser();
    this.isAdmin = this.authService.isAdmin();
    this.isDriver = this.authService.isDriver();
    this.isRegularUser = this.authService.isRegularUser();

    // Load driver status if user is a driver
    if (this.isDriver && this.currentUser?.userId) {
      this.loadDriverStatus();
    }

    // Subscribe to future changes
    this.authService.isAuthenticated$.subscribe(
      isAuth => {
        this.isAuthenticated = isAuth;
        if (!isAuth) {
          this.currentUser = null;
          this.driverStatusService.clearStatus();
        }
      }
    );

    this.authService.currentUser$.subscribe(
      user => {
        this.currentUser = user;
        this.isAdmin = this.authService.isAdmin();
        this.isDriver = this.authService.isDriver();

        if (this.isDriver && user?.userId) {
          this.loadDriverStatus();
        }
      }
    );

    this.driverStatusService.driverStatus$.subscribe(
      status => {
        if (status !== null) {
          this.driverActive = status;
        }
      }
    );
  }

  loadDriverStatus(): void {
    if (!this.currentUser?.userId) return;

    this.driverStatusService.getDriverStatus(this.currentUser.userId).subscribe({
      next: (driver) => {
        this.driverActive = driver.active || false;
        this.driverStatusService.updateLocalStatus(this.driverActive);
      },
      error: (error) => {
        console.error('Failed to load driver status', error);
      }
    });
  }

  toggleDriverStatus(): void {
    if (!this.currentUser?.userId || this.statusLoading) return;

    this.statusLoading = true;
    this.cdr.detectChanges();
    const newStatus = !this.driverActive;

    if (newStatus) {
      // Activate driver - need to get coordinates
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const coordinates: Coordinates = {
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          };
          this.driverService.activateDriver(coordinates).subscribe({
            next: () => {
              this.statusLoading = false;
              this.driverActive = true;
              this.driverStatusService.updateLocalStatus(true);
              this.notificationService.showNotification('Driver activated successfully', 'success');
              this.cdr.detectChanges();
            },
            error: (error) => {
              this.statusLoading = false;
              const message = error.error?.message || 'Failed to activate driver';
              this.notificationService.showNotification(message, 'error');
              this.cdr.detectChanges();
            }
          });
        },
        (error) => {
          this.statusLoading = false;
          console.warn('Geolocation error:', error);
          this.notificationService.showNotification('Unable to get your location. Please enable geolocation.', 'error');
          this.cdr.detectChanges();
        },
        {
          timeout: 5000,
          enableHighAccuracy: false
        }
      );
    } else {
      // Deactivate driver - using the driverStatusService which handles pending status
      this.driverStatusService.setDriverStatus(this.currentUser.userId, newStatus).subscribe({
        next: (response) => {
          this.statusLoading = false;

          // Check if the status change is pending
          if (response.pending) {
            this.notificationService.showNotification(
              response.message || 'Status change will be applied after your ride completes',
              'info'
            );
            // Don't update the local status yet, keep it as active
          } else if (response.active !== undefined) {
            this.driverActive = response.active;
            this.driverStatusService.updateLocalStatus(this.driverActive);
            this.notificationService.showNotification(
              `Status updated: ${this.driverActive ? 'Available' : 'Unavailable'}`,
              'success'
            );
          } else {
            this.notificationService.showNotification(
              response.message || 'Status change processed',
              'info'
            );
          }
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.statusLoading = false;
          const message = error.error?.message || 'Failed to update status';
          this.notificationService.showNotification(message, 'error');
          this.cdr.detectChanges();
        }
      });
    }
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/home-page']);
      },
      error: (error) => {
        if (error.status === 403) {
          const message = error.error?.message || 'Cannot logout during an active ride';
          this.notificationService.showNotification(message, 'error');
        } else {
          // For other errors, navigate to home
          this.router.navigate(['/home-page']);
        }
      }
    });
  }

  getLatestRide(){
    this.regularUserService.getLatestRideId()
      .pipe(
          timeout(5000) // 5000ms = 5 sekundi
        )
        .subscribe({
          next: ride => {
            this.latestRideId?.set(ride.rideId);
            this.latestRideStatus.set(ride.status)
            this.hasLatestRide.set(true);
            console.log(ride);
          },
          error: err => {
            if (err.name === 'TimeoutError') {
              console.error('Request timed out');
            } else {
              console.error(err);
            }
          },
      });
  }
}
