import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DestinationsDisplay } from "../destinations-display/destinations-display";
import { PassangersDisplay } from "../passangers-display/passangers-display";
import { Destination } from '@app/shared/models/destination';
import { Passenger } from '../add-passanger-input/add-passanger-input';
import { SelectedPreferencesDisplay } from '../selected-preferences-display/selected-preferences-display';
import { TripStatsDisplay } from '../trip-stats-display/trip-stats-display';
import { TripDestination } from '@app/shared/models/tripDestination';
import { GeocodingService } from '../geocoding-service/geocoding-service';
import { RouteEstimateInfo } from '@app/shared/models/route/routeEstimateInfo';

@Component({
  selector: 'app-trip-summary',
  imports: [CommonModule, DestinationsDisplay, PassangersDisplay, SelectedPreferencesDisplay, TripStatsDisplay],
  templateUrl: './trip-summary.html',
  styleUrl: './trip-summary.css',
})
export class TripSummary implements OnInit, OnChanges {
  @Input() destinations: TripDestination[] = [];
  @Input() passengers: Passenger[] = [];
  @Input() selectedPreferences: { vehicleType: string; isPetFriendly: boolean; isBabyFriendly: boolean } = {
    vehicleType: '',
    isPetFriendly: false,
    isBabyFriendly: false
  };
  @Input() estimateRouteInfo: RouteEstimateInfo | null = null;
  distanceString: string = '';
  estimatedTimeString: string = '';
  @Input() ridePrice: number = 0;
  priceString: string = '';
  @Output() destinationRemoved = new EventEmitter<string>();
  @Output() passengerRemoved = new EventEmitter<string>();
  @Output() finish = new EventEmitter<void>();
  @Output() canFinishChange = new EventEmitter<boolean>();

  canFinish = false;

  constructor(private geocodingService: GeocodingService) {}

  ngOnInit() {
    this.updateCanFinish();
    this.updateEstimateDisplay();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['destinations'] || changes['passengers'] || changes['selectedPreferences']) {
      this.updateCanFinish();
    }
    if (changes['estimateRouteInfo'] || changes['ridePrice']) {
      this.updateEstimateDisplay();
    }
  }

  private updateEstimateDisplay() {
    if (this.estimateRouteInfo) {
      var estimeRouteInfoFormatted = this.geocodingService.formatRouteInfo(this.estimateRouteInfo);
      this.distanceString = estimeRouteInfoFormatted.distanceKm + "km";
      this.estimatedTimeString = estimeRouteInfoFormatted.durationMin + "min";
      this.priceString = this.ridePrice + " RSD";
    }
  }

  private updateCanFinish() {
    this.canFinish = this.destinations.length >= 2 && this.selectedPreferences.vehicleType !== '';
    this.canFinishChange.emit(this.canFinish);
  }

  onFinish() {
    if (this.canFinish) {
      this.finish.emit();
    }
  }

  onDestinationRemoved(id: string) {
    this.destinationRemoved.emit(id);
  }

  onPassengerRemoved(id: string) {
    this.passengerRemoved.emit(id);
  }
}
