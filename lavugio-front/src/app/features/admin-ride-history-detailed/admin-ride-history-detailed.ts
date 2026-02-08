import { AfterViewInit, Component, inject, OnDestroy, signal, ViewChild } from '@angular/core';
import { BaseInfoPage } from '@app/features/base-info-page/base-info-page';
import { MapComponent } from '@app/shared/components/map/map';
import { RideHistoryAdminDetailedModel } from '@app/shared/models/ride/rideHistoryAdmin';
import { catchError, EMPTY, Subscription, timeout } from 'rxjs';
import { AdminService } from '@app/core/services/admin-service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-admin-ride-history-detailed',
  imports: [BaseInfoPage, MapComponent, CommonModule],
  templateUrl: './admin-ride-history-detailed.html',
  styleUrl: './admin-ride-history-detailed.css',
})
export class AdminRideHistoryDetailed implements AfterViewInit, OnDestroy {
  rideId!: number | null;
  ride = signal<RideHistoryAdminDetailedModel | null>(null);
  sub!: Subscription;
  adminService = inject(AdminService);
  activatedRoute = inject(ActivatedRoute);
  router = inject(Router);
  imageError = signal(false);
  @ViewChild('map') map!: MapComponent;

  ngAfterViewInit(): void {
    const rideIdStr = this.activatedRoute.snapshot.paramMap.get('rideId');
    if (rideIdStr) {
      this.rideId = parseInt(rideIdStr);
    } else {
      console.error("Couldn't parse ride id");
      return;
    }
    this.fetchRide();
  }

  fetchRide(): void {
    if (this.rideId == undefined) {
      console.error('RideId is undefined');
      return;
    }
    this.sub = this.adminService
      .getRideHistoryDetailed(this.rideId)
      .pipe(
        timeout(5000),
        catchError((err) => {
          console.error('Error fetching ride:', err);
          this.ride.set(null);
          return EMPTY;
        })
      )
      .subscribe((ride) => {
        this.ride.set(ride);
        this.map.setRoute(ride.checkpoints);
      });
  }

  getDriverPhotoUrl(): string {
    if (this.imageError()) return this.getPlaceholderUrl();
    const photoPath = this.ride()?.driverPhotoPath;
    if (photoPath && photoPath !== '' && !photoPath.includes('null')) {
      return `${environment.BACKEND_URL}${photoPath}`;
    }
    return this.getPlaceholderUrl();
  }

  getPlaceholderUrl(): string {
    const name = this.ride()?.driverName || '';
    const lastName = this.ride()?.driverLastName || '';
    const initials = `${name.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
    return `https://ui-avatars.com/api/?name=${initials}&background=606C38&color=fff&size=128`;
  }

  onImageError(): void {
    this.imageError.set(true);
  }

  getStarArray(rating: number | null): number[] {
    const stars = rating || 0;
    return Array(5).fill(0).map((_, i) => (i < stars ? 1 : 0));
  }

  goBack() {
    const email = this.activatedRoute.snapshot.queryParamMap.get('email');
    this.router.navigate(['/admin-ride-history'], {
      queryParams: email ? { email } : {}
    });
  }

  ngOnDestroy(): void {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }
}
