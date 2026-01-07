import { Component, Signal } from '@angular/core';
import { signal } from '@angular/core';

@Component({
  selector: 'app-ride-info',
  imports: [],
  templateUrl: './ride-info.html',
  styleUrl: './ride-info.css',
})
export class RideInfo {
  rideStatus = signal<string>('in_progress');
  ngOnInit(){
    setInterval( () => this.rideStatus.set('in_progress'), 1000);
    setInterval( () => this.rideStatus.set('cancelled'), 2000);
    setInterval( () => this.rideStatus.set('finished'), 3000);
    setInterval( () => this.rideStatus.set('scheduled'), 4000);
  }
}
