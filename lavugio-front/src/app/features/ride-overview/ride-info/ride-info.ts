import { Component } from '@angular/core';

@Component({
  selector: 'app-ride-info',
  imports: [],
  templateUrl: './ride-info.html',
  styleUrl: './ride-info.css',
})
export class RideInfo {
  rideStatus: string = 'cancelled';
}
