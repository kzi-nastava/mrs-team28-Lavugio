import { Component, OnInit, NgZone } from '@angular/core';
import { Links } from './links/links';
import { Logo } from './links/logo/logo';
import { Link } from './links/link/link';
import { AuthService, LoginResponse } from '@app/core/services/auth-service';
import { NotificationService } from '@app/core/services/notification-service';
import { DriverStatusService } from '@app/core/services/driver-status.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
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
  driverActive: boolean = false;
  statusLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router,
    private notificationService: NotificationService,
    private driverStatusService: DriverStatusService,
    private driverService: DriverService,
    private ngZone: NgZone
  ) {}

  ngOnInit() {
    // Check current authentication state
    this.isAuthenticated = this.authService.isAuthenticated();
    this.currentUser = this.authService.getStoredUser();
    this.isAdmin = this.authService.isAdmin();
    this.isDriver = this.authService.isDriver();
    
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
              this.ngZone.run(() => {
                this.statusLoading = false;
                this.driverActive = true;
                this.driverStatusService.updateLocalStatus(true);
                this.notificationService.showNotification('Driver activated successfully', 'success');
              });
            },
            error: (error) => {
              this.ngZone.run(() => {
                this.statusLoading = false;
                const message = error.error?.message || 'Failed to activate driver';
                this.notificationService.showNotification(message, 'error');
              });
            }
          });
        },
        (error) => {
          this.ngZone.run(() => {
            this.statusLoading = false;
            console.warn('Geolocation error:', error);
            this.notificationService.showNotification('Unable to get your location. Please enable geolocation.', 'error');
          });
        },
        {
          timeout: 5000,
          enableHighAccuracy: false
        }
      );
    } else {
      // Deactivate driver
      this.driverService.deactivateDriver().subscribe({
        next: () => {
          this.ngZone.run(() => {
            this.statusLoading = false;
            this.driverActive = false;
            this.driverStatusService.updateLocalStatus(false);
            this.notificationService.showNotification('Driver deactivated successfully', 'success');
          });
        },
        error: (error) => {
          this.ngZone.run(() => {
            this.statusLoading = false;
            const message = error.error?.message || 'Failed to deactivate driver';
            this.notificationService.showNotification(message, 'error');
          });
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
          const message = error.error?.message || 'Cannot logout at this time';
          this.notificationService.showNotification(message, 'error');
        } else {
          this.router.navigate(['/home-page']);
        }
      }
    });
  }
}
