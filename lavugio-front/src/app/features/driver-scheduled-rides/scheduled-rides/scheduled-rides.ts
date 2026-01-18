import { Component, input, output } from '@angular/core';
import { ScheduledRideDTO } from '@app/shared/models/scheduledRide';
import { Ride } from './ride/ride';
import { Coordinates } from '@app/shared/models/coordinates';

@Component({
  selector: 'app-scheduled-rides',
  imports: [Ride],
  templateUrl: './scheduled-rides.html',
  styleUrl: './scheduled-rides.css',
})
export class ScheduledRides {
  rides = input<ScheduledRideDTO[] | null>();
  clickedOutput = output<Coordinates[] | null>();
  hasActiveRide = input<boolean>();
  rideActionOutput = output<{action: string, rideId: number}>();

  clicked(coordinates : Coordinates[] | null){
    this.clickedOutput.emit(coordinates);
  }

  handleRideAction(event: {action: string, rideId: number}){
    this.rideActionOutput.emit(event);
  }

  canStartRide(index: number): boolean {
    const rides = this.rides();
    if (!rides || rides.length === 0) return false;
    
    // Samo prva vožnja (najranija) može da se startuje
    return index === 0;
  }
}