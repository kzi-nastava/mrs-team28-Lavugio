import { Component } from '@angular/core';
import { Passenger } from "./passenger/passenger";

@Component({
  selector: 'app-passengers',
  imports: [Passenger],
  templateUrl: './passengers.html',
  styleUrl: './passengers.css',
})
export class Passengers {

}

