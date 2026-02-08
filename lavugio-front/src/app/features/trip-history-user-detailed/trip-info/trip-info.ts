import { Component, input } from '@angular/core';
import { RideHistoryUserDetailedModel } from '@app/shared/models/ride/rideHistoryUserDetailed';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-trip-info',
  imports: [CommonModule],
  templateUrl: './trip-info.html',
  styleUrl: './trip-info.css',
})
export class TripInfo {
  info = input<RideHistoryUserDetailedModel | null>();
}
