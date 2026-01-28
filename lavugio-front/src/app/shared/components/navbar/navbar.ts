import { Component, OnInit, signal } from '@angular/core';
import { Links } from './links/links';
import { Logo } from './links/logo/logo';
import { Link } from './links/link/link';
import { AuthService, LoginResponse } from '@app/core/services/auth-service';
import { NotificationService } from '@app/core/services/notification-service';
import { DriverStatusService } from '@app/core/services/driver-status.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '@app/core/services/user/user-service';
import { timeout } from 'rxjs';

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
    const newStatus = !this.driverActive;
    
    this.driverStatusService.setDriverStatus(this.currentUser.userId, newStatus).subscribe({
      next: (response) => {
        this.statusLoading = false;
        if (response.active !== undefined) {
          this.driverActive = response.active;
          this.notificationService.showNotification(
            `Status updated: ${this.driverActive ? 'Available' : 'Unavailable'}`,
            'success'
          );
        } else {
          this.notificationService.showNotification(
            response.message || 'Status change pending',
            'info'
          );
        }
      },
      error: (error) => {
        this.statusLoading = false;
        const message = error.error?.message || 'Failed to update status';
        this.notificationService.showNotification(message, 'warning');
      }
    });
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
