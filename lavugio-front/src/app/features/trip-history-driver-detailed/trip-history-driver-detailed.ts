import { Component } from '@angular/core';
import { BaseInfoPage } from '@app/features/base-info-page/base-info-page';
import { Passengers } from './passengers/passengers';
import { TripInfo } from './trip-info/trip-info';

@Component({
  selector: 'app-trip-history-driver-detailed',
  imports: [BaseInfoPage, Passengers, TripInfo],
  templateUrl: './trip-history-driver-detailed.html',
  styleUrl: './trip-history-driver-detailed.css',
})
export class RideHistoryDriverDetailed {

}
