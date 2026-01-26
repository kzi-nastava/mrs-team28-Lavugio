import { Component, input } from '@angular/core';
import { RideHistoryDriverDetailed } from '../trip-history-driver-detailed';
import { RideHistoryDriverDetailedModel } from '@app/shared/models/ride/rideHistoryDriverDetailed';

@Component({
  selector: 'app-trip-info',
  imports: [],
  templateUrl: './trip-info.html',
  styleUrl: './trip-info.css',
})
export class TripInfo {
  info = input<RideHistoryDriverDetailedModel | null>();
}
