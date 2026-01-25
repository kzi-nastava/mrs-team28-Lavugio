import { Component, input } from '@angular/core';
import { PassengerModel } from '@app/shared/models/user/passenger';
import { Passenger } from "./passenger/passenger";

@Component({
  selector: 'app-passengers',
  templateUrl: './passengers.html',
  styleUrl: './passengers.css',
  imports: [Passenger],
})
export class Passengers {
  passengers = input<PassengerModel[]>();
}

