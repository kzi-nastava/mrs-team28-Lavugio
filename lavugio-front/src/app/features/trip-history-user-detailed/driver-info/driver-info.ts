import { Component, input, signal } from '@angular/core';
import { RideHistoryUserDetailedModel } from '@app/shared/models/ride/rideHistoryUserDetailed';
import { CommonModule } from '@angular/common';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-driver-info',
  imports: [CommonModule],
  templateUrl: './driver-info.html',
  styleUrl: './driver-info.css',
})
export class DriverInfo {
  info = input<RideHistoryUserDetailedModel | null>();
  imageError = signal(false);

  getDriverPhotoUrl(): string {
    if (this.imageError()) {
      return this.getPlaceholderUrl();
    }
    const photoPath = this.info()?.driverPhotoPath;
    if (photoPath && photoPath !== '' && !photoPath.includes('null')) {
      return `${environment.BACKEND_URL}${photoPath}`;
    }
    return this.getPlaceholderUrl();
  }

  getPlaceholderUrl(): string {
    const name = this.info()?.driverName || '';
    const lastName = this.info()?.driverLastName || '';
    const initials = `${name.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
    return `https://ui-avatars.com/api/?name=${initials}&background=606C38&color=fff&size=128`;
  }

  onImageError(): void {
    this.imageError.set(true);
  }
}
