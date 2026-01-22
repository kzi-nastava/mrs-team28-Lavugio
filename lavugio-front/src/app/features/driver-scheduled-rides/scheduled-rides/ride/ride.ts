import { Component, input, output } from '@angular/core';
import { Coordinates } from '@app/shared/models/coordinates';
import { ScheduledRideDTO } from '@app/shared/models/scheduledRide';

@Component({
  selector: 'app-ride',
  imports: [],
  templateUrl: './ride.html',
  styleUrl: './ride.css',
})
export class Ride {
  ride = input<ScheduledRideDTO | null>();
  clickedOutput = output<Coordinates[] | null>();
  hasActiveRide = input();
  canStart = input<boolean>();
  rideActionOutput = output<{action: string, rideId: number}>();

  clicked(){
    if (this.ride()?.checkpoints != null){
      this.clickedOutput.emit(this.ride()?.checkpoints!);
    }
  }

  formatDateTime(date: Date | null | undefined): string {
    if (!date) return "Loading...";
    date = new Date(date);
    const pad = (n: number) => n.toString().padStart(2, '0');

    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
    const day = pad(date.getDate());
    const month = pad(date.getMonth() + 1); 
    const year = date.getFullYear();

    return `${hours}:${minutes} ${day}.${month}.${year}`;
  }

  onStart(event: Event){
    event.stopPropagation();
    const rideId = this.ride()?.rideId;
    if (rideId) {
      this.rideActionOutput.emit({action: 'START', rideId});
    }
  }

  onPanic(event: Event){
    event.stopPropagation();
    const rideId = this.ride()?.rideId;
    if (rideId) {
      this.rideActionOutput.emit({action: 'PANIC', rideId});
    }
  }

  onFinish(event: Event){
    event.stopPropagation();
    const rideId = this.ride()?.rideId;
    if (rideId) {
      this.rideActionOutput.emit({action: 'FINISH', rideId});
    }

  }

  onFinishEarly(event: Event){
    event.stopPropagation();
    const rideId = this.ride()?.rideId;
    if (rideId) {
      this.rideActionOutput.emit({action: 'FINISH_EARLY', rideId});
    }
  }

  onDeny(event: Event){
    event.stopPropagation();
    const rideId = this.ride()?.rideId;
    if (rideId) {
      this.rideActionOutput.emit({action: 'DENY', rideId});
    }
  }
}